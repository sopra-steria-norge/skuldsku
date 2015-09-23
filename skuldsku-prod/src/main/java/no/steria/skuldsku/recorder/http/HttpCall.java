package no.steria.skuldsku.recorder.http;

import no.steria.skuldsku.recorder.java.serializer.ClassSerializer;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpCall implements Serializable {
    private String clientIdentifier = "";
    private String readInputStream;
    private Map<String,String> parameters = new HashMap<>();
    private String method;
    private String path;
    private String output;
    private int status = 0;
    private Map<String, List<String>> headers;

    public HttpCall setReadInputStream(String readInputStream) {
        this.readInputStream = readInputStream;
        return this;
    }
    
    public String getClientIdentifier() {
        return clientIdentifier;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public void setClientIdentifier(String clientIdentifier) {
        this.clientIdentifier = (clientIdentifier != null) ? clientIdentifier : "";
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

    public HttpCall setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getPath() {
        return path;
    }

    public HttpCall setPath(String path) {
        this.path = path;
        return this;
    }

    public String getOutput() {
        return output;
    }

    public HttpCall setOutput(String output) {
        this.output = output;
        return this;
    }

    public String serializedString() {
        String serialized = new ClassSerializer().asString(this);
        return serialized;
    }

    public static HttpCall parseFromString(String serializedStr) {
        return (HttpCall) new ClassSerializer().asObject(serializedStr);
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}