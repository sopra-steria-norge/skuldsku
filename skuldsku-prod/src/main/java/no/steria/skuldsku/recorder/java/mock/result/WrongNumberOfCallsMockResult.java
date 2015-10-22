package no.steria.skuldsku.recorder.java.mock.result;

import no.steria.skuldsku.common.result.ItemResult;
import no.steria.skuldsku.recorder.java.JavaCall;

public class WrongNumberOfCallsMockResult implements ItemResult<JavaCall> {

    private final JavaCall javaCall;
    private final int expectedCalls;
    private final int actualCalls;
    
    public WrongNumberOfCallsMockResult(JavaCall javaCall, int expectedCalls, int actualCalls) {
        this.javaCall = javaCall;
        this.expectedCalls = expectedCalls;
        this.actualCalls = actualCalls;
    }
    
    @Override
    public String getStartTime() {
        return javaCall.getStartTime();
    }
    
    @Override
    public String getRequestId() {
        return javaCall.getClientIdentifier();
    }
    
    @Override
    public String getTitle() {
        return "Java Mock " + getItem().getClassName() + "." + getItem().getMethodname() + " actual number of calls (" + getActualCalls() + ") does not match expected (" + getExpectedCalls() + ")";
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public String getExplanation() {
        return "Number of actual method calls (" + actualCalls + ") does not match expected (" + expectedCalls + ").";
    }
    
    public int getExpectedCalls() {
        return expectedCalls;
    }
    
    public int getActualCalls() {
        return actualCalls;
    }

    @Override
    public JavaCall getItem() {
        return javaCall;
    }
    
    @Override
    public String toString() {
        return "Number of actual method calls does not match expected. Expected:\n"
                + expectedCalls
                + "\nGot:\n"
                + actualCalls
                + "\n\nClass: "
                + javaCall.getClassName()
                + "\nMethod: "
                + javaCall.getMethodname()
                + "\nArgs: "
                + javaCall.getParameters();
    }

}