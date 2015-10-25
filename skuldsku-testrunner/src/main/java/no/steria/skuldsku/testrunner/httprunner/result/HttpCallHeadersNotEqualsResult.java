package no.steria.skuldsku.testrunner.httprunner.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import no.steria.skuldsku.common.result.ComparisionResult;
import no.steria.skuldsku.recorder.http.HttpCall;

public class HttpCallHeadersNotEqualsResult implements ComparisionResult<HttpCall>{

    private final HttpCall expected;
    private final HttpCall actual;
    
    
    public HttpCallHeadersNotEqualsResult(HttpCall expected, HttpCall actual) {
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
        return "HTTP " + getActual().getMethod() + " " + getActual().getPath() + " actual response headers does not match expected";
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public String getExplanation() {
        return "Actual response headers does not match expected.";
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
        return getExplanation() + " Expected:\n" + getFiltredResponseHeadersAsString(expected.getResponseHeaders()) + "\nGot:\n" + getFiltredResponseHeadersAsString(actual.getResponseHeaders());
    }

    public static String getFiltredResponseHeadersAsString(Map<String, List<String>> responseHeaders) {
        final StringBuilder sb = new StringBuilder();
        final List<String> keys = new ArrayList<String>(responseHeaders.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            for (String value : responseHeaders.get(key)) {
                if (key.equals("Set-Cookie") && value.startsWith("JSESSIONID")
                        || key.equals("Expires")
                        || key.equals("ETag")
                        || key.equals("Last-Modified")) {
                    sb.append(key + ": [VALUE IGNORED]\n");
                } else {
                    sb.append(key + ": " + value + "\n");
                }
            }
        }
        return sb.toString();
    }
}
