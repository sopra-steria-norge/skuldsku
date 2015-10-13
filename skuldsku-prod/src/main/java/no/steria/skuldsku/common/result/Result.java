package no.steria.skuldsku.common.result;

public interface Result {

    public String getRequestId();
    public boolean isFailure();
    public String getTitle();
    public String getExplanation();
    public String getStartTime();
    
}
