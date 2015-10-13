package no.steria.skuldsku.testrunner.httprunner.result;

import no.steria.skuldsku.common.result.ComparisionResult;
import no.steria.skuldsku.recorder.http.HttpCall;

public class HttpCallResult implements ComparisionResult<HttpCall>{

    private final HttpCall expected;
    private final HttpCall actual;
    private final int requestNumber;
    
    public HttpCallResult(HttpCall expected, HttpCall actual, int requestNumber) {
        this.expected = expected;
        this.actual = actual;
        this.requestNumber = requestNumber;
        
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
        return "HTTP " + getActual().getMethod() + " " + getActual().getPath();
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public String getExplanation() {
        return "";
    }

    @Override
    public HttpCall getExpected() {
        return expected;
    }

    @Override
    public HttpCall getActual() {
        return actual;
    }
    
    public int getRequestNumber() {
        return requestNumber;
    }
    
    @Override
    public String toString() {
        return "EXPECTED:\n" + expected.toString() + "\n\nACTUAL:\n" + actual.toString();
    }

}
