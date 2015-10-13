package no.steria.skuldsku.common.result;

public class AssertionResult implements Result {

    private final String title;
    private final String explanation;
    
    public AssertionResult(String title, String explanation) {
        this.title = title;
        this.explanation = explanation;
    }
    
    @Override
    public String getStartTime() {
        return null;
    }
    
    @Override
    public String getRequestId() {
        return "";
    }
    
    @Override
    public String getTitle() {
        return title;
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
