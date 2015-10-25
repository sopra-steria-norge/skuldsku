package no.steria.skuldsku.testrunner.httprunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class RequestCompleteData {

    private final String requestMethod;
    private final String requestPath;
    private final Map<String, List<String>> requestHeaders;
    private final String requestInput;
    
    private final int responseStatus;
    private final Map<String, List<String>> responseHeaders;
    private final String responseOutput;
    
    
    public RequestCompleteData(String requestMethod, String requestPath, Map<String, List<String>> requestHeaders, String requestInput, int responseStatus, Map<String, List<String>> responseHeaders, String responseOutput) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.requestHeaders = Collections.unmodifiableMap(requestHeaders); // TODO: Deep unmodifiable.
        this.requestInput = requestInput;
        this.responseStatus = responseStatus;
        this.responseHeaders = responseHeaders;
        this.responseOutput = responseOutput;
    }

    
    

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Map<String, List<String>> getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestInput() {
        return requestInput;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseOutput() {
        return responseOutput;
    }
}
