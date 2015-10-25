package no.steria.skuldsku.testrunner.httprunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RequestData {
    
    private String requestMethod;
    private String requestPath;
    private Map<String, List<String>> requestHeaders;
    private String requestInput;

    
    public RequestData(String requestMethod, String requestPath, Map<String, List<String>> requestHeaders, String requestInput) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.requestHeaders = deepCloneMap(requestHeaders);
        this.requestInput = requestInput;
    }


    public String getRequestMethod() {
        return requestMethod;
    }


    public void setRequestMethod(String method) {
        this.requestMethod = method;
    }


    public String getRequestPath() {
        return requestPath;
    }


    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }


    public Map<String, List<String>> getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestInput() {
        return requestInput;
    }


    public void setRequestInput(String requestInput) {
        this.requestInput = requestInput;
    } 

    /* TODO: Stateful and share one object .. or stateless? */
    private Map<String, List<String>> deepCloneMap(Map<String, List<String>> map) {
        Map<String, List<String>> result = new HashMap<>();

        for (String key : map.keySet()) {
            result.put(key,new ArrayList<>(map.get(key)));
        } return result;
    }

}
