package no.steria.skuldsku.recorder.java.mock.result;

import no.steria.skuldsku.common.result.ComparisionResult;
import no.steria.skuldsku.recorder.java.JavaCall;

public class ComparisionMockResult implements ComparisionResult<JavaCall> {

    private final JavaCall expected;
    private final String expectedStrippedArgs;
    private final JavaCall actual;
    private final String actualStrippedArgs;
    
    
    public ComparisionMockResult(JavaCall expected, String expectedStrippedArgs, JavaCall actual, String actualStrippedArgs) {
        this.expected = expected;
        this.actual = actual;
        this.expectedStrippedArgs = expectedStrippedArgs;
        this.actualStrippedArgs = actualStrippedArgs;
    }
    
    
    @Override
    public String getRequestId() {
        return actual.getClientIdentifier();
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public String getExplanation() {
        return "Could not find a recorded JavaCall matching the provided arguments.";
    }

    @Override
    public JavaCall getExpected() {
        return expected;
    }
    
    public String getExpectedStrippedArgs() {
        return expectedStrippedArgs;
    }
    
    @Override
    public JavaCall getActual() {
        return actual;
    }
    
    public String getActualStrippedArgs() {
        return actualStrippedArgs;
    }
    
    @Override
    public String toString() {
        return "No mock data found. Interface: " + actual.getClassName() + " Method: " + actual.getMethodname() + " Args:\n" + actualStrippedArgs + "\nClosest match has args:\n" + expectedStrippedArgs;
    }

}
