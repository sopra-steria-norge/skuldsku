package no.steria.copito.httpspy;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportObject implements Serializable {
    private String readInputStream;
    private Map<String,String> parameters = new HashMap<>();
    private String method;
    private String path;
    private String output;
    private Map<String, List<String>> headers;

    public ReportObject setReadInputStream(String readInputStream) {
        this.readInputStream = readInputStream;
        return this;
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

    public ReportObject setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getPath() {
        return path;
    }

    public ReportObject setPath(String path) {
        this.path = path;
        return this;
    }

    public String getOutput() {
        return output;
    }

    public ReportObject setOutput(String output) {
        this.output = output;
        return this;
    }

    public String serializedString() {
        String serialized = new ClassSerializer().asString(this);
        return serialized;
    }

    public static ReportObject parseFromString(String serializedStr) {
        return (ReportObject) new ClassSerializer().asObject(serializedStr);
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}
