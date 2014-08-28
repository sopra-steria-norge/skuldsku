package no.steria.skuldsku.recorder.javainterfacerecorder.serializer;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClassSerializer {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private List<Object> knownObjects = new ArrayList<>();

    private int isKnown(Object obj) {
        for (int i=0;i<knownObjects.size();i++) {
            if (knownObjects.get(i) == obj) {
                return i;
            }
        }
        return -1;
    }

    public String asString(Object object) {
        return new ClassSerializer().myAsString(object);
    }

    private String myAsString(Object object) {
        String encodedValue = encodeValue(object);
        if (object != null && !encodedValue.startsWith("<")) {
            return "<" + object.getClass().getName() + ";" + encodedValue + ">";
        }
        return encodedValue;
    }

    public Object asObject(String serializedValue) {

        return new ClassSerializer().myAsObject(serializedValue);
    }


    private Object myAsObject(String serializedValue) {
        if ("<null>".equals(serializedValue)) {
            return null;
        }
        String[] parts = splitToParts(serializedValue);

        if ("list".equals(parts[0]) || "map".equals(parts[0])) {
            return objectValueFromString(serializedValue, null);
        }

        if ("duplicate".equals(parts[0])) {
            int index = Integer.parseInt(parts[1]);
            return knownObjects.get(index);
        }

        if (!serializedValue.contains("=")) {
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
                Field field = object.getClass().getDeclaredField(fieldName);

                setFieldValue(object, encFieldValue, field);

            } catch (NoSuchFieldException | IllegalAccessError e) {
                throw new RuntimeException(e);
            }
        }

        return object;
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
                    .replaceAll("&amp", "&")
                    .replaceAll("&semi", ";")
                    .replaceAll("&eq", "=")
                    .replaceAll("&lt", "<")
                    .replaceAll("&gt", ">");
        }
        return value;
    }

    private String encodeValue(Object fieldValue) {
        if (fieldValue == null) {
            return "<null>";
        }

        if (fieldValue instanceof Enum) {
            Enum en = (Enum) fieldValue;

            return String.format("<%s;%s>", en.getClass().getName(), en.name());
        }
        if ("org.joda.time.DateTime".equals(fieldValue.getClass().getName())) {
            try {
                Method toDate = fieldValue.getClass().getMethod("toDate");
                Date asDate = (Date) toDate.invoke(fieldValue);
                return dateFormat.format(asDate);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        Package aPackage = fieldValue.getClass().getPackage();
        String packageName = aPackage != null ? aPackage.getName() : null;
        if (!isValueClass(packageName)) {
            int ind = isKnown(fieldValue);
            if (ind != -1) {
                return "<duplicate;" + ind + ">";
            }
        }
        knownObjects.add(fieldValue);
        if (fieldValue instanceof Object[]) {
            Object[] arr = (Object[]) fieldValue;
            StringBuilder res = new StringBuilder("<array");
            for (Object objInArr : arr) {
                encode(res, objInArr);
            }
            res.append(">");
            return res.toString();
        }
        if (fieldValue instanceof List) {
            List<Object> listValues = (List<Object>) fieldValue;
            StringBuilder res = new StringBuilder("<list");
            for (Object objectInList : listValues) {
                encode(res, objectInList);
            }
            res.append(">");
            return res.toString();
        }
        if (fieldValue instanceof Map) {
            Map<Object, Object> mapValue = (Map<Object, Object>) fieldValue;
            StringBuilder res = new StringBuilder("<map");
            for (Map.Entry<Object, Object> entry : mapValue.entrySet()) {
                Object val = entry.getKey();
                encode(res, val);
                val = entry.getValue();
                encode(res, val);

            }
            res.append(">");
            return res.toString();
        }
        if (Date.class.equals(fieldValue.getClass())) {
            return dateFormat.format(fieldValue);
        }

        if (isValueClass(packageName)) {
            return fieldValue.toString()
                    .replaceAll("&", "&amp")
                    .replaceAll(";", "&semi")
                    .replaceAll("<", "&lt")
                    .replaceAll(">", "&gt")
                    .replaceAll("=", "&eq");
        }
        String classname = fieldValue.getClass().getName();
        String fieldsCode = computeFields(fieldValue);
        return "<" + classname + fieldsCode + ">";
    }

    private boolean isValueClass(String packageName) {
        return "java.lang".equals(packageName) || "java.util".equals(packageName) || "java.math".equals(packageName);
    }

    private String computeFields(Object object) {
        Field[] declaredFields = object.getClass().getDeclaredFields();
        StringBuilder result = new StringBuilder();
        for (Field field : declaredFields) {
            result.append(";");
            result.append(field.getName());
            result.append("=");
            try {
                boolean access = field.isAccessible();
                if (!access) {
                    field.setAccessible(true);
                }
                Object fieldValue = field.get(object);
                String encodedValue = encodeValue(fieldValue);
                result.append(encodedValue);
                if (!access) {
                    field.setAccessible(false);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result.toString();
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
                try {
                    Class<?> clazz = Class.forName(className);
                    Constructor<?> c = clazz.getDeclaredConstructor();
                    c.setAccessible(true);
                    return c.newInstance();
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });
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
