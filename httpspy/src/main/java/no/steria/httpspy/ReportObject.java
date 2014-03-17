package no.steria.httpspy;

import java.util.HashMap;
import java.util.Map;

public class ReportObject {
    private String readInputStream;
    private Map<String,String> parameters = new HashMap<>();
    private String method;

    public void setReadInputStream(String readInputStream) {
        this.readInputStream = readInputStream;
    }

    public String getReadInputStream() {
        return readInputStream;
    }

    public Map<String,String> getParametersRead() {
        return parameters;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
