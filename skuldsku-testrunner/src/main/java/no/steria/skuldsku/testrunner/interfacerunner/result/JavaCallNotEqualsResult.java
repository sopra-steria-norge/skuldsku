package no.steria.skuldsku.testrunner.interfacerunner.result;

import no.steria.skuldsku.common.result.ComparisionResult;
import no.steria.skuldsku.recorder.java.JavaCall;

public class JavaCallNotEqualsResult implements ComparisionResult<JavaCall> {

    private final JavaCall expected;
    private final JavaCall actual;
    
    public JavaCallNotEqualsResult(JavaCall expected, JavaCall actual) {
        this.expected = expected;
        this.actual = actual;
    }
    
    
    @Override
    public String getStartTime() {
        return actual.getStartTime();
    }
    
    @Override
    public String getRequestId() {
        return actual.getClientIdentifier();
    }
    
    @Override
    public String getTitle() {
        return "Java Call " + getExpected().getClassName() + "." + getExpected().getMethodname() + " actual does not match expected";
    }
    

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public String getExplanation() {
        return "Actual JavaCall does not match the expected value.";
    }

    @Override
    public JavaCall getExpected() {
        return expected;
    }
    
    @Override
    public JavaCall getActual() {
        return actual;
    }
    
    @Override
    public String toString() {
        return "Actual JavaCall does not match the expected value.\nExpected:\n" + expected.toString() + "\nGot:\n" + actual.toString();
    }
    
}
