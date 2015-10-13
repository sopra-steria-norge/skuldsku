package no.steria.skuldsku.recorder.java.mock;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.steria.skuldsku.common.result.Result;
import no.steria.skuldsku.common.result.Results;
import no.steria.skuldsku.common.result.ResultsProvider;
import no.steria.skuldsku.recorder.common.ClientIdentifierHolder;
import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.recorder.java.mock.result.ComparisionMockResult;
import no.steria.skuldsku.recorder.java.mock.result.MissingMockResult;
import no.steria.skuldsku.recorder.java.mock.result.MockResult;
import no.steria.skuldsku.recorder.java.serializer.ClassSerializer;
import no.steria.skuldsku.recorder.logging.RecorderLog;

/**
 * A mock for a single class.
 */
public class RecordedDataMock implements MockInterface, ResultsProvider {
    private static final Set<String> globalIgnoreFields = new HashSet<String>();
    
    private final List<JavaCall> recorded;
    private String serviceClass;
    private final Results mockResults = new Results();
    private final Map<JavaCall, Integer> expectedCalls = new HashMap<>();
    private final Map<JavaCall, Integer> actualCalls = new HashMap<>();
    

    /**
     * Initializes a new mock.
     * @param recorded the <code>JavaCall</code>s to be used as mock data. Note that
     *          the class is not checked when comparing calls, so subclasses need their
     *          own mock if one wishes to separate the mocking behaviour.
     */
    public RecordedDataMock(List<JavaCall> recorded) {
        if (recorded == null) {
            throw new NullPointerException("recorded == null");
        }
        
        this.recorded = new ArrayList<>();
        
        /*
         * JavaCalls can be equal (with or without parameter filtering). The code below
         * keeps only one JavaCall when there are multiple equal calls, and uses the
         * "expectedCalls" variable to store how many copies there were.
         */
        final ClassSerializer standardSerializer = new ClassSerializer();
        final ClassSerializer fieldIgnorerSerializer = createFieldIgnorerSerializer();
        for (JavaCall jc : recorded) {
            final String jcArgs = removeIgnoredFields(jc.getParameters(), standardSerializer, fieldIgnorerSerializer);
            final JavaCall existingJc = findExisting(this.recorded, jc.getMethodname(), jcArgs, standardSerializer, fieldIgnorerSerializer);
            if (existingJc == null) {
                expectedCalls.put(jc, 1);
                actualCalls.put(jc, 0);
                this.recorded.add(jc);
            } else {
                expectedCalls.put(existingJc, expectedCalls.get(existingJc) + 1);
            }
        }
        
        assert expectedCalls.size() == actualCalls.size();
        assert this.recorded.size() == expectedCalls.size();
    }

    private static JavaCall findExisting(List<JavaCall> exisingList, String methodName, String jcArgs, ClassSerializer standardSerializer, ClassSerializer fieldIgnorerSerializer) {
        for (JavaCall existingJc : exisingList) {
            if (existingJc.getMethodname().equals(methodName)) {
                final String existingJcArgs = removeIgnoredFields(existingJc.getParameters(), standardSerializer, fieldIgnorerSerializer);
                if (existingJcArgs.equals(jcArgs)) {
                    return existingJc;
                }
            }
        }
        return null;
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
        final ClassSerializer fieldIgnorerSerializer = createFieldIgnorerSerializer();
        
        final String currentFilteredArgs = fieldIgnorerSerializer.asString(args);
        for (JavaCall candidateJavaCall : recorded) {
            if (method.getName().equals(candidateJavaCall.getMethodname())) {
                final String candidateFilteredArgs = removeIgnoredFields(candidateJavaCall.getParameters(), standardSerializer, fieldIgnorerSerializer);
                if (currentFilteredArgs.equals(candidateFilteredArgs)) {
                    actualCalls.put(candidateJavaCall, actualCalls.get(candidateJavaCall) + 1);
                    if (candidateJavaCall.getThrown() != null) {
                        final Throwable t = (Throwable) standardSerializer.asObject(candidateJavaCall.getThrown());
                        if (t != null) {
                            throw t;
                        }
                    }
                    return standardSerializer.asObject(candidateJavaCall.getResult());
                }
            }
        }

        throw handleMissingMockData(interfaceClass, method, args, standardSerializer, fieldIgnorerSerializer);
    }

    private ClassSerializer createFieldIgnorerSerializer() {
        final ClassSerializer fieldIgnorerSerializer = new ClassSerializer();
        fieldIgnorerSerializer.addAllIgnoreField(globalIgnoreFields);
        return fieldIgnorerSerializer;
    }
    
    @Override
    public Results getResults() {
        return Results.combine(mockResults, findMissingCalls());
    }
    
    private Results findMissingCalls() {
        final Results results = new Results();
        for (JavaCall jc : expectedCalls.keySet()) {
            final int expected = expectedCalls.get(jc);
            final int actual = actualCalls.get(jc);
            if (expected != actual) {
                final long time = System.currentTimeMillis();
                final JavaCall actualJavaCall = new JavaCall(ClientIdentifierHolder.getClientIdentifier(),
                        jc.getClassName(),
                        jc.getMethodname(),
                        jc.getParameters(),
                        jc.getResult(),
                        jc.getThrown(),
                        time,
                        time);
                
                results.addResult(new MissingMockResult(actualJavaCall, expected, actual));
            }
        }
        return results;
    }

    private RuntimeException handleMissingMockData(Class<?> interfaceClass, Method method, Object[] args,
            final ClassSerializer standardSerializer, final ClassSerializer fieldIgnorerSerializer) {
        final String currentArgsAsString = fieldIgnorerSerializer.asString(args);
        
        final JavaCall closestMatch = findClosestMatch(method, currentArgsAsString, standardSerializer, fieldIgnorerSerializer);
        final Result mockResult;
        final RuntimeException missingMockDataException;

        final long time = System.currentTimeMillis();
        if (closestMatch != null) {
            final String closestMatchArgs = removeIgnoredFields(closestMatch.getParameters(), standardSerializer, fieldIgnorerSerializer);
            missingMockDataException = new RuntimeException("No mock data found. Interface: " + interfaceClass.getName() + " Method: " + method.getName() + " Args:\n" + currentArgsAsString + "\nClosest args in expected:\n" + closestMatchArgs);
            final JavaCall actual = new JavaCall(ClientIdentifierHolder.getClientIdentifier(),
                    interfaceClass.getName(),
                    method.getName(),
                    args,
                    null,
                    missingMockDataException,
                    time,
                    time);
            mockResult = new ComparisionMockResult(closestMatch, closestMatchArgs, actual, currentArgsAsString);
        } else {
            missingMockDataException = new RuntimeException("No mock data found. Interface: " + interfaceClass.getName() + " Method: " + method.getName() + " Args:\n" + currentArgsAsString);
            final JavaCall actual = new JavaCall(ClientIdentifierHolder.getClientIdentifier(),
                    interfaceClass.getName(),
                    method.getName(),
                    args,
                    null,
                    missingMockDataException,
                    time,
                    time);
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
