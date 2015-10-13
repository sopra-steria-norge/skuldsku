package no.steria.skuldsku.recorder.java.mock.result;

import no.steria.skuldsku.common.result.ItemResult;
import no.steria.skuldsku.recorder.java.JavaCall;

public class MockResult implements ItemResult<JavaCall> {

    private final JavaCall javaCall;
    private final String strippedArgs;
    
    public MockResult(JavaCall javaCall, String strippedArgs) {
        this.javaCall = javaCall;
        this.strippedArgs = strippedArgs;
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
        return "Java Mock " + getItem().getClassName() + "." + getItem().getMethodname() + " additional in actual";
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
    public JavaCall getItem() {
        return javaCall;
    }
    
    @Override
    public String toString() {
        return "No mock data found. Interface: " + javaCall.getClassName() + " Method: " + javaCall.getMethodname() + " Args:\n" + strippedArgs;
    }

}
