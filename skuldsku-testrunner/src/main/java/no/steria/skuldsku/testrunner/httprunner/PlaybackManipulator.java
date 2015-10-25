package no.steria.skuldsku.testrunner.httprunner;

public interface PlaybackManipulator {
    
    public default void performRequestManipulation(RequestData requestData) {
        
    }
    
    public default void reportRequestCompleteData(RequestCompleteData requestCompleteData) {
        
    }

}
