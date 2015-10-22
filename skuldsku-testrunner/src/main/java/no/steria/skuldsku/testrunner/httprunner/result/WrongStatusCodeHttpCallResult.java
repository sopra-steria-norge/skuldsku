package no.steria.skuldsku.testrunner.httprunner.result;

import no.steria.skuldsku.common.result.ComparisionResult;
import no.steria.skuldsku.recorder.http.HttpCall;

public class WrongStatusCodeHttpCallResult implements ComparisionResult<HttpCall>{

    private final HttpCall expected;
    private final HttpCall actual;
    
    public WrongStatusCodeHttpCallResult(HttpCall expected, HttpCall actual) {
        this.expected = expected;
        this.actual = actual;
        
        if (actual.getStartTime() == null) {
            throw new NullPointerException("actual.getStartTime() == null");
        }
        if (actual.getClientIdentifier() == null) {
            throw new NullPointerException("actual.getClientIdentifier() == null");
        }
        if (actual.getPath() == null) {
            throw new NullPointerException("actual.getPath() == null");
        }
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
        return "HTTP " + getActual().getMethod() + " " + getActual().getPath() + " actual status code does not match expected";
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public String getExplanation() {
        return "Actual status code (" + actual.getStatus() + ") does not match expected (" + actual.getStatus() + ")";
    }

    @Override
    public HttpCall getExpected() {
        return expected;
    }

    @Override
    public HttpCall getActual() {
        return actual;
    }
    
    @Override
    public String toString() {
        return getExplanation();
    }

}
