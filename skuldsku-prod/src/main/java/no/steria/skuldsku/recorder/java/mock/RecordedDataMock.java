package no.steria.skuldsku.recorder.java.mock;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.recorder.java.serializer.ClassSerializer;

public class RecordedDataMock implements MockInterface {
    private static final Set<String> globalIgnoreFields = new HashSet<String>();
    
    private final List<JavaCall> recorded;
    private String serviceClass;

    // Receives a list of recordings to be played back
    public RecordedDataMock(List<JavaCall> recorded) {
        this.recorded = recorded != null ? recorded : new ArrayList<JavaCall>();

    }

    public String getImplementation() {
        return recorded.get(0).getClassName();
    }
    
    /**
     * Adds a field to be ignored when matching JavaInterfaceCall.
     * 
     * @param ignoreField The field on the format: package.class.field.
     *                    Example: <code>"com.example.MyClass.myField"</code>
     */
    public static void addGlobalIgnoreField(String ignoreField) {
        globalIgnoreFields.add(ignoreField);
    }

    @Override
    public Object invoke(Class<?> interfaceClass, String serviceObjectName, Method method, Object[] args) {
        final ClassSerializer standardSerializer = new ClassSerializer();
        
        final ClassSerializer fieldIgnorerSerializer = new ClassSerializer();
        fieldIgnorerSerializer.addAllIgnoreField(globalIgnoreFields);
        
        String currentArgsAsString = fieldIgnorerSerializer.asString(args);

        for (JavaCall recordObject : recorded) {
            // TODO: FIX (choose correct subclass+method combination): serviceObjectName.equals(recordObject.getClassName())
            if (method.getName().equals(recordObject.getMethodname())) {
                final String recordedArgsAsString = fieldIgnorerSerializer.asString(standardSerializer.asObject(recordObject.getParameters()));
                if (currentArgsAsString.equals(recordedArgsAsString)) {
                    return standardSerializer.asObject(recordObject.getResult());
                }
            }
        }
        
        final String closestMatch = findClosestMatch(method, currentArgsAsString, standardSerializer, fieldIgnorerSerializer);
        final String extraString = (closestMatch == null) ? "" : "\nDid not match recorded-args:\n" + closestMatch;

        throw new RuntimeException("No mock data found. Interface: " + interfaceClass.getName() + " Method: " + method.getName() + " Args:\n" + fieldIgnorerSerializer.asString(args) + extraString);
    }

    private String findClosestMatch(final Method method, String currentArgsAsString, final ClassSerializer standardSerializer, final ClassSerializer fieldIgnorerSerializer) {
        String closestArgMatch = null;
        int bestCommonFields = 0;
        for (JavaCall recordObject : recorded) {
            // TODO: FIX (choose correct subclass+method combination): serviceObjectName.equals(recordObject.getClassName())
            if (method.getName().equals(recordObject.getMethodname())) {
                final String recordedArgsAsString = fieldIgnorerSerializer.asString(standardSerializer.asObject(recordObject.getParameters()));
                final String[] a1 = recordedArgsAsString.split(";");
                final String[] a2 = currentArgsAsString.split(";");
                int commonFields = determineCommonFields(a1, a2);
                if (commonFields > bestCommonFields) {
                    bestCommonFields = commonFields;
                    closestArgMatch = recordedArgsAsString;
                }
            }
        }
        return closestArgMatch;
    }
    
    public int determineCommonFields(Object[] a1, Object[] a2) {
        int count = 0;
        for (int i=0; i<Math.min(a1.length, a2.length); i++) {
            if (a1[i].equals(a2[i])) {
                count++;
            }
        }
        return count;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }
}
