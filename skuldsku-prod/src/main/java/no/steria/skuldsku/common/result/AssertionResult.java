package no.steria.skuldsku.common.result;

public class AssertionResult implements Result {

    private String explanation;
    
    public AssertionResult(String explanation) {
        this.explanation = explanation;
    }
    
    
    @Override
    public String getRequestId() {
        return "";
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public String getExplanation() {
        return explanation;
    }

}
