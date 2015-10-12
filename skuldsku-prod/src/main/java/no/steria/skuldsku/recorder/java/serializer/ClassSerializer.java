package no.steria.skuldsku.recorder.java.serializer;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.steria.skuldsku.recorder.logging.RecorderLog;

public class ClassSerializer {
    private static final Set<String> globalIgnoreFields = new HashSet<String>();
    private static Set<Class<?>> nonDuplicationClass = new HashSet<>();
    static {
        nonDuplicationClass.add(String.class);
    }
    
    private final Set<String> ignoreFields;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private List<Object> knownObjects = new ArrayList<>();

    
    public ClassSerializer() {
        this(new HashSet<String>());
    }
    
    private ClassSerializer(Set<String> ignoreFields) {
        this.ignoreFields = ignoreFields;
    }

    
    public static void addNonDuplicationClass(Class<?> clazz) {
        nonDuplicationClass.add(clazz);
    }
    
    public static void removeNonDuplicationClass(Class<?> clazz) {
        nonDuplicationClass.remove(clazz);
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
        StringBuilder encodedValue = encodeValue(object, false);
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
            if ("&null".equals(serializedValue)) {
                return null;
            }
            String[] parts = splitToParts(serializedValue);

            if ("list".equals(parts[0])) {
                final Object collection = createList(serializedValue);
                return collection;
            }

            if ("map".equals(parts[0])) {
                final Object collection = createMap(serializedValue);
                return collection;
            }
            
            if ("array".equals(parts[0])) {
                try {
                    final Object array = createArray(serializedValue);
                    return array;
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
                    final String value = parts[1];
                    final Class<?> type = Class.forName(parts[0]);
                    if (value.startsWith("<")) {
                        final Object objectValue = myAsObject(value);
                        return objectValue;
                    } else {                  
                        final Object objectValue = objectValueFromString(value, type);
                        return objectValue;
                    }
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
            final String serializedValueDisplay = reduceToMaxLength(serializedValue, 10000);
            throw new RuntimeException("Could not deserialize:\n" + serializedValueDisplay + "\n" + e.getMessage(), e);
        }
    }

    private String reduceToMaxLength(String serializedValue, int maxLength) {
        final String serializedValueDisplay;
        if (serializedValue.length() > maxLength) {
            serializedValueDisplay = serializedValue.substring(0, maxLength - 3) + "...";
        } else {
            serializedValueDisplay = serializedValue;
        }
        return serializedValueDisplay;
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
        if (fieldValue.startsWith("<")) {
            return myAsObject(fieldValue);
        }
        
        final Object value;
        if ("&null".equals(fieldValue)) {
            value = null;
        } else if (type.isEnum()) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            final Object e = Enum.valueOf((Class) type, fieldValue);
            return e;
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
                value = constructor.newInstance(val);
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException | ParseException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else if (BigDecimal.class.equals(type)) {
            value = new BigDecimal(Double.parseDouble(fieldValue));
        } else {
            value = unescapeSpecialCharacters(fieldValue);
        }
        
        if (value != null && (type == null || !type.isPrimitive())) {
            knownObjects.add(value);
        }
        
        return value;
    }
    
    public String unescapeSpecialCharacters(String s) {
        return s.replaceAll("&semi", ";")
                .replaceAll("&eq", "=")
                .replaceAll("&lt", "<")
                .replaceAll("&gt", ">")
                .replaceAll("&percent","%")
                .replaceAll("&newline", "\n")
                .replaceAll("&amp", "&"); //This really must happen last, or we end up double-deserializing.
    }

    private StringBuilder encodeValue(Object fieldValue, boolean primitive) {
        StringBuilder stringBuffer = new StringBuilder();
        if (fieldValue == null) {
            stringBuffer.append("<null>");
            return stringBuffer;
        }

        if (fieldValue instanceof Enum) {
            Enum<?> en = (Enum<?>) fieldValue;
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
        
        final String className = fieldValue.getClass().getName();
        
        if (!nonDuplicationClass.contains(fieldValue.getClass())) {
            int ind = isKnown(fieldValue);
            if (ind != -1) {
                return stringBuffer.append("<duplicate;").append(ind).append(">");
            }
        }
            
        if (fieldValue != null && !primitive) {
            knownObjects.add(fieldValue);
        }
        if (fieldValue.getClass().isArray()) {
            StringBuilder res = new StringBuilder("<array;");
            res.append(escapeSpecialCharacters(fieldValue.getClass().getName()));
            for (int i = 0; i < Array.getLength(fieldValue); i++) {
                Object objInArr = Array.get(fieldValue, i);
                encode(res, objInArr, fieldValue.getClass().getComponentType().isPrimitive());
            }
            res.append(">");
            return res;
        }
        if (fieldValue instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> listValues = (List<Object>) fieldValue;
            StringBuilder res = new StringBuilder("<list");
            for (Object objectInList : listValues) {
                encode(res, objectInList, false);
            }
            res.append(">");
            return res;
        }
        if (fieldValue instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> mapValue = (Map<Object, Object>) fieldValue;
            StringBuilder res = new StringBuilder("<map");
            try {
                for (Map.Entry<Object, Object> entry : mapValue.entrySet()) {
                    Object val = entry.getKey();
                    encode(res, val, false);
                    val = entry.getValue();
                    encode(res, val, false);

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

        if (isStringValueClass(className)) {
            return new StringBuilder(escapeSpecialCharacters(fieldValue.toString()));
        }
        String classname = fieldValue.getClass().getName();
        StringBuilder fieldsCode = computeFields(fieldValue);
        return new StringBuilder("<").append(classname).append(fieldsCode).append(">");
    }
    
    private String escapeSpecialCharacters(String s) {
        return s.replaceAll("&", "&amp")
                .replaceAll(";", "&semi")
                .replaceAll("<", "&lt")
                .replaceAll(">", "&gt")
                .replaceAll("=", "&eq")
                .replaceAll("%","&percent")
                .replaceAll("\r\n", "&newline")
                .replaceAll("\n\r", "&newline")
                .replaceAll("\r", "&newline")
                .replaceAll("\n", "&newline");
    }

    private boolean isStringValueClass(String className) {
        return Boolean.class.getName().equals(className)
                || Byte.class.getName().equals(className)
                || Character.class.getName().equals(className)
                || Class.class.getName().equals(className) // TODO
                || Double.class.getName().equals(className)
                || Enum.class.getName().equals(className) // TODO?
                || Float.class.getName().equals(className)
                || Integer.class.getName().equals(className)
                || Long.class.getName().equals(className)
                || Number.class.getName().equals(className)
                || Short.class.getName().equals(className)
                || String.class.getName().equals(className)
                || StringBuffer.class.getName().equals(className)
                || StringBuilder.class.getName().equals(className)
                || Date.class.getName().equals(className) // TODO
                || BigDecimal.class.getName().equals(className)
                || BigInteger.class.getName().equals(className)
                || MathContext.class.getName().equals(className);
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
                StringBuilder encodedValue = encodeValue(fieldValue, field.getType().isPrimitive());
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
    
    private Object createArray(String fieldValue) throws ClassNotFoundException {
        final String[] parts = splitToParts(fieldValue);
        assert "array".equals(parts[0]);
        
        Class<?> type = Class.forName(unescapeSpecialCharacters(parts[1]));
        final int reservedFieldsCount = 2;
        
        Object arr = Array.newInstance(type.getComponentType(), parts.length - reservedFieldsCount);
        
        knownObjects.add(arr);

        for (int i = 0; i < parts.length - reservedFieldsCount; i++) {
            String codeStr = parts[i + reservedFieldsCount];
            if(type.getComponentType().isPrimitive()) {
                final String[] valType = splitToParts(codeStr);
                final Class<?> aClass;
                try {
                    aClass = Class.forName(valType[0]);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                Array.set(arr, i, objectValueFromString(valType[1], aClass));
            } else {
                Array.set(arr, i, objectValueFromString(codeStr, null));
            }
        }

        return arr;
    }
    
    private Object createList(String fieldValue) {
        final String[] parts = splitToParts(fieldValue);
        final List<Object> resList = new ArrayList<>();
        
        knownObjects.add(resList);

        for (int i = 1; i < parts.length; i++) {
            resList.add(myAsObject(parts[i]));
        }

        return resList;        
    }
    
    private Object createMap(String fieldValue) {
        final String[] parts = splitToParts(fieldValue);
        final Map<Object, Object> resMap = new HashMap<>();
        
        knownObjects.add(resMap);

        for (int i = 1; i < parts.length; i++) {
            Object key = myAsObject(parts[i]);
            i++;
            Object value = myAsObject(parts[i]);
            resMap.put(key, value);
        }

        return resMap;
    }

    private void encode(StringBuilder res, Object val, boolean primitive) {
        res.append(";");
        if (val == null) {
            res.append("&null");
            return;
        }
        final String encodeValue = encodeValue(val, primitive).toString();
        if (encodeValue.startsWith("<")) {
            res.append(encodeValue);
        } else {
            res.append("<").append(val.getClass().getName()).append(";").append(encodeValue).append(">");
        }
    }
}
