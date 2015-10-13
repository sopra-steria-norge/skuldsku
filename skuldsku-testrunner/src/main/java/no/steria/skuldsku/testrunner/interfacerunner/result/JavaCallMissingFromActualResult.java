package no.steria.skuldsku.testrunner.interfacerunner.result;

import no.steria.skuldsku.common.result.ItemResult;
import no.steria.skuldsku.recorder.java.JavaCall;

public class JavaCallMissingFromActualResult implements ItemResult<JavaCall> {

    private final JavaCall javaCall;
    
    public JavaCallMissingFromActualResult(JavaCall javaCall) {
        this.javaCall = javaCall;
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
        return "Java Call " + getItem().getClassName() + "." + getItem().getMethodname() + " missing from actual";
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public String getExplanation() {
        return "Missing Java Call in actual.";
    }

    @Override
    public JavaCall getItem() {
        return javaCall;
    }
    
    @Override
    public String toString() {
        return "Missing Java Call in actual:\n" + javaCall.toString();
    }
    

}
