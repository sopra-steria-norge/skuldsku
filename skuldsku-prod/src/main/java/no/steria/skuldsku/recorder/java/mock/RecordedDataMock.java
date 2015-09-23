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
                final String recordedArgsAsString = fieldIgnorerSerializer.asString(standardSerializer.asObject(recordObject.getParameters()));;
                if (currentArgsAsString.equals(recordedArgsAsString)) {
                    return standardSerializer.asObject(recordObject.getResult());
                }
            }
        }
        
        // TODO: Log a WARNING.
        return null;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }
}
