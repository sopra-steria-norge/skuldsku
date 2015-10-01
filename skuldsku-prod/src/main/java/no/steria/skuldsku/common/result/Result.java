package no.steria.skuldsku.common.result;

public interface Result {

    public String getRequestId();
    public boolean isFailure();
    public String getExplanation();
    
}
