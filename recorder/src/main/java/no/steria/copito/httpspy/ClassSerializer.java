package no.steria.copito.httpspy;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

public class ClassSerializer {
    private final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("YYYYMMddHHmmssSSS");

    public String asString(Object object) {
        String encval = encodeValue(object);
        if (object != null && !encval.startsWith("<")) {
            return "<" + object.getClass().getName() + ";" + encval + ">";
        }
        return encval;
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



    public Object asObject(String serializedValue) {
        if ("<null>".equals(serializedValue)) {
            return null;
        }
        String[] parts = splitToParts(serializedValue);

        if (serializedValue.indexOf("=") == -1) {
            try {
                Class<?> clazz=null;
                if ("list".equals(parts[0]) || "map".equals(parts[0])) {
                    return objectValueFromString(serializedValue,null);
                }
                return objectValueFromString(parts[1], Class.forName(parts[0]));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        Object object = initObject(parts[0]);

        for (int i=1;i<parts.length;i++) {
            //String[] fieldParts = parts[i].split("=");
            int eqPos=parts[i].indexOf("=");
            String fieldName = parts[i].substring(0,eqPos);
            String encFieldValue = parts[i].substring(eqPos+1);

            try {
                Field field = object.getClass().getDeclaredField(fieldName);

                setFieldValue(object, encFieldValue, field);

            } catch (NoSuchFieldException | IllegalAccessError e) {
                throw new RuntimeException(e);
            }
        }

        return object;
    }

    public String[] splitToParts(String serializedValue) {
        List<String> result = new ArrayList<>();

        int level = 0;
        int prevpos=0;
        for (int pos=0;pos<serializedValue.length();pos++) {
            Character c = serializedValue.charAt(pos);
            if (c == '<') {
                level++;
                if (level == 1) {
                    prevpos=pos+1;
                }
                continue;
            }
            if (c == '>') {
                level--;
                if (level == 0) {
                    result.add(serializedValue.substring(prevpos,pos));
                    prevpos=pos+1;
                }
                continue;
            }
            if (c == ';' && level == 1) {
                result.add(serializedValue.substring(prevpos,pos));
                prevpos=pos+1;
                continue;
            }
        }



        return result.toArray(new String[0]);
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

    private Object initObject(String classname) {
        try {
            return Class.forName(classname).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Object objectValueFromString(String fieldValue, Class<?> type) {
        Object value;

        if ("&null".equals(fieldValue)) {
            value = null;
        } else if (fieldValue.startsWith("<")) {
            value = complexValueFromString(fieldValue,type);
        } else if (int.class.equals(type) || Integer.class.equals(type)) {
            value = Integer.parseInt(fieldValue);
        } else if (long.class.equals(type) || Long.class.equals(type)) {
            value = Long.parseLong(fieldValue);
        } else if (char.class.equals(type) || Character.class.equals(type)) {
            value = fieldValue.charAt(0);
        } else if (double.class.equals(type) || Double.class.equals(type)) {
            value = Double.parseDouble(fieldValue);
        } else if (Date.class.equals(type)) {
            value = dateFormat.parseLocalDateTime(fieldValue).toDate();
        } else if (DateTime.class.equals(type)) {
            value = dateFormat.parseDateTime(fieldValue);
        } else if (BigDecimal.class.equals(type)) {
            value = new BigDecimal(Double.parseDouble(fieldValue));
        } else {
            value = fieldValue
                    .replaceAll("&amp","&")
                    .replaceAll("&semi", ";")
                    .replaceAll("&eq", "=")
                    .replaceAll("&lt", "<")
                    .replaceAll("&gt", ">");
        }
        return value;
    }

    private Object complexValueFromString(String fieldValue, Class<?> type) {
        String[] parts = splitToParts(fieldValue);
        if ("array".equals(parts[0])) {
            Object arr = (Object[]) Array.newInstance(type.getComponentType(), parts.length - 1);

            for (int i=0;i<parts.length-1;i++) {
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

            for (int i=0;i<parts.length-1;i++) {
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
            Map<Object,Object> resMap = new HashMap<>();

            for (int i=0;i<parts.length-1;i++) {
                Object key = extractObject(parts[i + 1]);
                i++;
                Object value = extractObject(parts[i +  1]);
                resMap.put(key,value);
            }

            return resMap;
        }

        return asObject(fieldValue);
    }

    private Object extractObject(String part) {
        if ("&null".equals(part)) {
            return null;
        }
        String codeStr = part;
        String[] valType = splitToParts(codeStr);
        Class<?> aClass;
        try {
            aClass = Class.forName(valType[0]);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return objectValueFromString(valType[1], aClass);
    }


    public String encodeValue(Object fieldValue) {
        if (fieldValue == null) {
            return "<null>";
        }
        if (fieldValue instanceof Object[]) {
            Object[] arr=(Object[]) fieldValue;
            StringBuilder res = new StringBuilder("<array");
            for (Object objInArr : arr) {
                encode(res, objInArr);
            }
            res.append(">");
            return res.toString();
        }
        if (fieldValue instanceof  List) {
            List<Object> listValues = (List<Object>) fieldValue;
            StringBuilder res = new StringBuilder("<list");
            for (Object objectInList : listValues) {
                encode(res, objectInList);
            }
            res.append(">");
            return res.toString();
        }
        if (fieldValue instanceof Map) {
            Map<Object,Object> mapValue= (Map<Object, Object>) fieldValue;
            StringBuilder res = new StringBuilder("<map");
            for (Map.Entry<Object,Object> entry : mapValue.entrySet()) {
                Object val = entry.getKey();
                encode(res, val);
                val = entry.getValue();
                encode(res, val);

            }
            res.append(">");
            return res.toString();
        }
        if (fieldValue == null) {
            return "&null";
        }
        if (Date.class.equals(fieldValue.getClass())) {
            return dateFormat.print(new DateTime(fieldValue));
        }
        if (DateTime.class.equals(fieldValue.getClass())) {
            return dateFormat.print((ReadableInstant) fieldValue);
        }
        String packageName = fieldValue.getClass().getPackage().getName();
        if ("java.lang".equals(packageName) || "java.util".equals(packageName) || "java.math".equals(packageName)) {
            return fieldValue.toString()
                    .replaceAll("&","&amp")
                    .replaceAll(";","&semi")
                    .replaceAll("<","&lt")
                    .replaceAll(">","&gt")
                    .replaceAll("=","&eq");
        }
        String classname = fieldValue.getClass().getName();
        String fieldsCode = computeFields(fieldValue);
        return "<" + classname + fieldsCode + ">";
    }

    private void encode(StringBuilder res, Object val) {
        res.append(";");
        if (val == null) {
            res.append("&null");
            return;
        }
        res.append("<" + val.getClass().getName() + ";" + encodeValue(val) + ">");
    }

}
