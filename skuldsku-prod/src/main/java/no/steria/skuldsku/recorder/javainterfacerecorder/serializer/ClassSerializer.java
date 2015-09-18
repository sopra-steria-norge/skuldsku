package no.steria.skuldsku.recorder.javainterfacerecorder.serializer;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.steria.skuldsku.recorder.logging.RecorderLog;

public class ClassSerializer {
    private static final Set<String> globalIgnoreFields = new HashSet<String>();
    private final Set<String> ignoreFields;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private List<Object> knownObjects = new ArrayList<>();
    
    public ClassSerializer() {
        ignoreFields = new HashSet<String>();
    }
    
    private ClassSerializer(Set<String> ignoreFields) {
        this.ignoreFields = ignoreFields;
    }

    /**
     * Adds a field to be ignored (skipped) when serializing.
     * 
     * @param ignoreField The field on the format: package.class.field.
     *                    Example: <code>"com.example.MyClass.myField"</code>
     */
    public static void addGlobalIgnoreField(String ignoreField) {
        globalIgnoreFields.add(ignoreField);
    }

    /**
     * Adds a field to be ignored (skipped) when serializing.
     * 
     * @param ignoreField The field on the format: package.class.field.
     *                    Example: <code>"com.example.MyClass.myField"</code>
     */
    public void addIgnoreField(String ignoreField) {
        ignoreFields.add(ignoreField);
    }
    
    public void addAllIgnoreField(Set<String> ignoreFields) {
        this.ignoreFields.addAll(ignoreFields);
    }
    
    private int isKnown(Object obj) {
        for (int i=0;i<knownObjects.size();i++) {
            if (knownObjects.get(i) == obj) {
                return i;
            }
        }
        return -1;
    }

    public String asString(Object object) {
        return new ClassSerializer(ignoreFields).myAsString(object).toString();
    }

    private StringBuilder myAsString(Object object) {
        StringBuilder encodedValue = encodeValue(object);
        if (object != null && encodedValue.indexOf("<") != 0) {
            encodedValue.append(">");
            encodedValue.insert(0, ";");
            encodedValue.insert(0, object.getClass().getName());
            encodedValue.insert(0, "<");
        }
        return encodedValue;
    }

    public Object asObject(String serializedValue) {
        return new ClassSerializer(ignoreFields).myAsObject(serializedValue);
    }


    private Object myAsObject(String serializedValue) {
        try {
            if ("<null>".equals(serializedValue)) {
                return null;
            }
            String[] parts = splitToParts(serializedValue);

            if ("list".equals(parts[0]) || "map".equals(parts[0])) {
                return objectValueFromString(serializedValue, null);
            }

            if ("array".equals(parts[0])) {
                try {
                    return objectValueFromString(serializedValue, Class.forName("[L" + splitToParts(parts[1])[0] + ";"));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            if ("duplicate".equals(parts[0])) {
                int index = Integer.parseInt(parts[1]);
                return knownObjects.get(index);
            }

            if (!serializedValue.contains("=") && parts.length > 1) {
                try {
                    return objectValueFromString(parts[1], Class.forName(parts[0]));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            Object object = initObject(parts[0]);

            knownObjects.add(object);

            for (int i = 1; i < parts.length; i++) {
                //String[] fieldParts = parts[i].split("=");
                int eqPos = parts[i].indexOf("=");
                String fieldName = parts[i].substring(0, eqPos);
                String encFieldValue = parts[i].substring(eqPos + 1);

                try {
                    Field field = findField(object.getClass(),fieldName);

                    setFieldValue(object, encFieldValue, field);

                } catch (IllegalAccessError e) {
                    throw new RuntimeException(e);
                }
            }

            return object;
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not deserialize:\n" + serializedValue, e);
        }
    }

    private Field findField(Class<?> clazz, String fieldName)  {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return findField(superclass,fieldName);
            }
            throw new RuntimeException(e);
        }
    }

    private String[] splitToParts(String serializedValue) {
        List<String> result = new ArrayList<>();

        int level = 0;
        int prevpos = 0;
        for (int pos = 0; pos < serializedValue.length(); pos++) {
            Character c = serializedValue.charAt(pos);
            if (c == '<') {
                level++;
                if (level == 1) {
                    prevpos = pos + 1;
                }
                continue;
            }
            if (c == '>') {
                level--;
                if (level == 0) {
                    result.add(serializedValue.substring(prevpos, pos));
                    prevpos = pos + 1;
                }
                continue;
            }
            if (c == ';' && level == 1) {
                result.add(serializedValue.substring(prevpos, pos));
                prevpos = pos + 1;
            }
        }
        return result.toArray(new String[result.size()]);
    }

    private Object objectValueFromString(String fieldValue, Class<?> type) {
        Object value;

        if ("&null".equals(fieldValue)) {
            value = null;
        } else if (fieldValue.startsWith("<")) {
            value = complexValueFromString(fieldValue, type);
        } else if (type.isEnum()) {
            Class<? extends Enum> enuClazz = (Class<? extends Enum>) type;
            return Enum.valueOf(enuClazz, fieldValue);
        } else if (int.class.equals(type) || Integer.class.equals(type)) {
            value = Integer.parseInt(fieldValue);
        } else if (long.class.equals(type) || Long.class.equals(type)) {
            value = Long.parseLong(fieldValue);
        } else if (char.class.equals(type) || Character.class.equals(type)) {
            value = fieldValue.charAt(0);
        } else if (double.class.equals(type) || Double.class.equals(type)) {
            value = Double.parseDouble(fieldValue);
        } else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
            value = Boolean.parseBoolean(fieldValue);
        } else if (Date.class.equals(type)) {
            try {
                value = dateFormat.parse(fieldValue);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else if ("org.joda.time.DateTime".equals(type.getName())) {
            try {
                Date val = dateFormat.parse(fieldValue);
                Constructor<?> constructor = type.getConstructor(Object.class);
                return constructor.newInstance(val);
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException | ParseException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else if (BigDecimal.class.equals(type)) {
            value = new BigDecimal(Double.parseDouble(fieldValue));
        } else {
            value = fieldValue
                    .replaceAll("&semi", ";")
                    .replaceAll("&eq", "=")
                    .replaceAll("&lt", "<")
                    .replaceAll("&gt", ">")
                    .replaceAll("&percent","%")
                    .replaceAll("&newline", "\n")
                    .replaceAll("&amp", "&"); //This really must happen last, or we end up double-deserializing.
        }
        return value;
    }

    private StringBuilder encodeValue(Object fieldValue) {
        StringBuilder stringBuffer = new StringBuilder();
        if (fieldValue == null) {
            stringBuffer.append("<null>");
            return stringBuffer;
        }

        if (fieldValue instanceof Enum) {
            Enum en = (Enum) fieldValue;
            stringBuffer.append(String.format("<%s;%s>", en.getClass().getName(), en.name()));
            return stringBuffer;
        }
        if ("org.joda.time.DateTime".equals(fieldValue.getClass().getName())) {
            try {
                Method toDate = fieldValue.getClass().getMethod("toDate");
                Date asDate = (Date) toDate.invoke(fieldValue);
                stringBuffer.append(dateFormat.format(asDate));
                return stringBuffer;
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        Package aPackage = fieldValue.getClass().getPackage();
        String packageName = aPackage != null ? aPackage.getName() : null;
        if (!isValueClass(packageName)) {
            int ind = isKnown(fieldValue);
            if (ind != -1) {
                return stringBuffer.append("<duplicate;").append(ind).append(">");
            }
        }
        knownObjects.add(fieldValue);
        if (fieldValue.getClass().isArray()) {
            StringBuilder res = new StringBuilder("<array");

            for (int i = 0; i < Array.getLength(fieldValue); i++) {
                Object objInArr = Array.get(fieldValue, i);
                encode(res, objInArr);
            }
            res.append(">");
            return res;
        }
        if (fieldValue instanceof List) {
            List<Object> listValues = (List<Object>) fieldValue;
            StringBuilder res = new StringBuilder("<list");
            for (Object objectInList : listValues) {
                encode(res, objectInList);
            }
            res.append(">");
            return res;
        }
        if (fieldValue instanceof Map) {
            Map<Object, Object> mapValue = (Map<Object, Object>) fieldValue;
            StringBuilder res = new StringBuilder("<map");
            try {
                for (Map.Entry<Object, Object> entry : mapValue.entrySet()) {
                    Object val = entry.getKey();
                    encode(res, val);
                    val = entry.getValue();
                    encode(res, val);

                }
            } catch (UnsupportedOperationException uoe) {
                RecorderLog.error("Could not serialize map that does not support entrySet(). Skipping value.");
            }
            res.append(">");
            return res;
        }
        if (Date.class.equals(fieldValue.getClass())) {
            return new StringBuilder(dateFormat.format(fieldValue));
        }

        if (isValueClass(packageName)) {
            return new StringBuilder(fieldValue.toString()
                    .replaceAll("&", "&amp")
                    .replaceAll(";", "&semi")
                    .replaceAll("<", "&lt")
                    .replaceAll(">", "&gt")
                    .replaceAll("=", "&eq")
                    .replaceAll("%","&percent")
                    .replaceAll("\r\n", "&newline")
                    .replaceAll("\n\r", "&newline")
                    .replaceAll("\r", "&newline")
                    .replaceAll("\n", "&newline"));
        }
        String classname = fieldValue.getClass().getName();
        StringBuilder fieldsCode = computeFields(fieldValue);
        return new StringBuilder("<").append(classname).append(fieldsCode).append(">");
    }

    private boolean isValueClass(String packageName) {
        return "java.lang".equals(packageName) || "java.util".equals(packageName) || "java.math".equals(packageName);
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        for (Field field: type.getDeclaredFields()) {
            fields.add(field);
        }
        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

    private StringBuilder computeFields(Object object) {
        List<Field> declaredFields = getAllFields(new ArrayList<Field>(),object.getClass());
        StringBuilder result = new StringBuilder();
        for (Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            
            final String fieldName = field.getDeclaringClass().getName() + "." + field.getName();
            if (ignoreFields.contains(fieldName)
                    || globalIgnoreFields.contains(fieldName)) {
                continue;
            }
            result.append(";");
            result.append(field.getName());
            result.append("=");
            try {
                boolean access = field.isAccessible();
                if (!access) {
                    field.setAccessible(true);
                }
                Object fieldValue = field.get(object);
                StringBuilder encodedValue = encodeValue(fieldValue);
                result.append(encodedValue);
                if (!access) {
                    field.setAccessible(false);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private void setFieldValue(Object object, String fieldValue, Field field) {
        Object value;
        value = objectValueFromString(fieldValue, field.getType());

        boolean access = field.isAccessible();
        if (!access) {
            field.setAccessible(true);
        }
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (!access) {
            field.setAccessible(false);
        }
    }

    private Object initObject(final String className) {
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                final Class<?> clazz;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                
                try {
                    Constructor<?> c = clazz.getDeclaredConstructor();
                    c.setAccessible(true);
                    return c.newInstance();
                } catch (Exception e) {
                    return createObjectUsingFallback(clazz);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <T> T createObjectUsingFallback(final Class<T> clazz) {
        /* XXX: Use a proper solution like the Objenesis library */
        try {
            Class<?> unsafeClazz = Class.forName("sun.misc.Unsafe");
            Field declaredField = unsafeClazz.getDeclaredField("theUnsafe");
            declaredField.setAccessible(true);
            Object unsafe = declaredField.get(null);
            final Method allocateInstance = unsafeClazz.getMethod("allocateInstance", new Class[] { Class.class });
            return (T) allocateInstance.invoke(unsafe, clazz);
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e2) {
            throw new RuntimeException(e2);
        }
    }

    private Object complexValueFromString(String fieldValue, Class<?> type) {
        String[] parts = splitToParts(fieldValue);
        if ("array".equals(parts[0])) {
            Object arr = Array.newInstance(type.getComponentType(), parts.length - 1);

            for (int i = 0; i < parts.length - 1; i++) {
                String codeStr = parts[i + 1];
                String[] valType = splitToParts(codeStr);
                Class<?> aClass;
                try {
                    aClass = Class.forName(valType[0]);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                Array.set(arr, i, objectValueFromString(valType[1], aClass));
            }

            return arr;
        }
        if ("list".equals(parts[0])) {
            List<Object> resList = new ArrayList<>();

            for (int i = 0; i < parts.length - 1; i++) {
                String codeStr = parts[i + 1];
                String[] valType = splitToParts(codeStr);
                Class<?> aClass;
                try {
                    aClass = Class.forName(valType[0]);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                resList.add(objectValueFromString(valType[1], aClass));
            }

            return resList;
        }
        if ("map".equals(parts[0])) {
            Map<Object, Object> resMap = new HashMap<>();

            for (int i = 0; i < parts.length - 1; i++) {
                Object key = extractObject(parts[i + 1]);
                i++;
                Object value = extractObject(parts[i + 1]);
                resMap.put(key, value);
            }

            return resMap;
        }

        return myAsObject(fieldValue);
    }

    private Object extractObject(String part) {
        if ("&null".equals(part)) {
            return null;
        }
        String[] valType = splitToParts(part);
        Class<?> aClass;
        try {
            aClass = Class.forName(valType[0]);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return objectValueFromString(valType[1], aClass);
    }

    private void encode(StringBuilder res, Object val) {
        res.append(";");
        if (val == null) {
            res.append("&null");
            return;
        }
        res.append("<").append(val.getClass().getName()).append(";").append(encodeValue(val)).append(">");
    }
}
