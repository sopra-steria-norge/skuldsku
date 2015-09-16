package no.steria.skuldsku.testrunner.httprunner;

public class FailingRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FailingRequestException(String message) {
        super(message);
    }
    
}
