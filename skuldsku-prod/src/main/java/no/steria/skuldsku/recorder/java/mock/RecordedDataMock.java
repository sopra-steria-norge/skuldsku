package no.steria.skuldsku.recorder.java.mock;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.steria.skuldsku.common.result.Results;
import no.steria.skuldsku.common.result.Result;
import no.steria.skuldsku.common.result.ResultsProvider;
import no.steria.skuldsku.recorder.common.ClientIdentifierHolder;
import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.recorder.java.mock.result.ComparisionMockResult;
import no.steria.skuldsku.recorder.java.mock.result.MockResult;
import no.steria.skuldsku.recorder.java.serializer.ClassSerializer;

public class RecordedDataMock implements MockInterface, ResultsProvider {
    private static final Set<String> globalIgnoreFields = new HashSet<String>();
    
    private final List<JavaCall> recorded;
    private String serviceClass;
    private final Results mockResults = new Results();
    

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
    public Object invoke(Class<?> interfaceClass, String serviceObjectName, Method method, Object[] args) throws Throwable {
        final ClassSerializer standardSerializer = new ClassSerializer();
        
        final ClassSerializer fieldIgnorerSerializer = new ClassSerializer();
        fieldIgnorerSerializer.addAllIgnoreField(globalIgnoreFields);
        
        final String currentArgsAsString = fieldIgnorerSerializer.asString(args);
        for (JavaCall recordObject : recorded) {
            // TODO: FIX (choose correct subclass+method combination): serviceObjectName.equals(recordObject.getClassName())
            if (method.getName().equals(recordObject.getMethodname())) {
                final String recordedArgsAsString = removeIgnoredFields(recordObject.getParameters(), standardSerializer, fieldIgnorerSerializer);
                if (currentArgsAsString.equals(recordedArgsAsString)) {
                    if (recordObject.getThrown() != null) {
                        final Throwable t = (Throwable) standardSerializer.asObject(recordObject.getThrown());
                        if (t != null) {
                            throw t;
                        }
                    }
                    return standardSerializer.asObject(recordObject.getResult());
                }
            }
        }

        throw handleMissingMockData(interfaceClass, method, args, standardSerializer, fieldIgnorerSerializer);
    }
    
    @Override
    public Results getResults() {
        return mockResults;
    }

    private RuntimeException handleMissingMockData(Class<?> interfaceClass, Method method, Object[] args,
            final ClassSerializer standardSerializer, final ClassSerializer fieldIgnorerSerializer) {
        final String currentArgsAsString = fieldIgnorerSerializer.asString(args);
        
        final JavaCall closestMatch = findClosestMatch(method, currentArgsAsString, standardSerializer, fieldIgnorerSerializer);
        final Result mockResult;
        final RuntimeException missingMockDataException;

        if (closestMatch != null) {
            final String closestMatchArgs = removeIgnoredFields(closestMatch.getParameters(), standardSerializer, fieldIgnorerSerializer);
            missingMockDataException = new RuntimeException("No mock data found. Interface: " + interfaceClass.getName() + " Method: " + method.getName() + " Args:\n" + currentArgsAsString + "\nClosest args in expected:\n" + closestMatchArgs);
            final JavaCall actual = new JavaCall(ClientIdentifierHolder.getClientIdentifier(),
                    interfaceClass.getName(),
                    method.getName(),
                    args,
                    null,
                    missingMockDataException);
            mockResult = new ComparisionMockResult(closestMatch, closestMatchArgs, actual, currentArgsAsString);
        } else {
            missingMockDataException = new RuntimeException("No mock data found. Interface: " + interfaceClass.getName() + " Method: " + method.getName() + " Args:\n" + currentArgsAsString);
            final JavaCall actual = new JavaCall(ClientIdentifierHolder.getClientIdentifier(),
                    interfaceClass.getName(),
                    method.getName(),
                    args,
                    null,
                    missingMockDataException);
            mockResult = new MockResult(actual, currentArgsAsString);
        }
        
        synchronized (mockResults) {
            mockResults.addResult(mockResult);
        }

        return missingMockDataException;
    }

    private static String removeIgnoredFields(String s, ClassSerializer standardSerializer, ClassSerializer fieldIgnorerSerializer) {
        return fieldIgnorerSerializer.asString(standardSerializer.asObject(s));
    }

    private JavaCall findClosestMatch(final Method method, String currentArgsAsString, final ClassSerializer standardSerializer, final ClassSerializer fieldIgnorerSerializer) {
        JavaCall closestMatch = null;
        int bestCommonFields = 0;
        for (JavaCall recordObject : recorded) {
            // TODO: FIX (choose correct subclass+method combination): serviceObjectName.equals(recordObject.getClassName())
            if (method.getName().equals(recordObject.getMethodname())) {
                final String recordedArgsAsString = removeIgnoredFields(recordObject.getParameters(), standardSerializer, fieldIgnorerSerializer);
                final String[] a1 = recordedArgsAsString.split(";");
                final String[] a2 = currentArgsAsString.split(";");
                int commonFields = determineCommonFields(a1, a2);
                if (commonFields > bestCommonFields) {
                    bestCommonFields = commonFields;
                    closestMatch = recordObject;
                }
            }
        }
        return closestMatch;
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
