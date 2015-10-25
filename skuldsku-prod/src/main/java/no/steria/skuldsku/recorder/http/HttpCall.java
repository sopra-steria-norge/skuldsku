package no.steria.skuldsku.recorder.http;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import no.steria.skuldsku.recorder.java.serializer.ClassSerializer;
import no.steria.skuldsku.recorder.recorders.FileRecorderReader;

public class HttpCall implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String clientIdentifier = ""; // requestId
    private String readInputStream; // requestData
    private Map<String,String> parameters = new HashMap<>();  // UNUSED?
    private String method; // requestMethod
    private String path; // requestPath
    private String output; // responseData
    private int status = 0; // responseStatus
    private Map<String, List<String>> headers; // requestHeaders
    private Map<String, List<String>> responseHeaders;
    private String startTime;
    private String endTime;

    public HttpCall setReadInputStream(String readInputStream) {
        this.readInputStream = readInputStream;
        return this;
    }
    
    /**
     * Gets the time the HTTP request arrived on the server.
     * @return The point in time using the format <code>yyyy-MM-dd'T'HH:mm:ss.SSSXXX</code>.
     */
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        this.startTime = simpleDateFormat.format(startTime);
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    /**
     * Gets the time the HTTP response was given by the server.
     * @return The point in time using the format <code>yyyy-MM-dd'T'HH:mm:ss.SSSXXX</code>.
     */
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(long endTime) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        this.endTime = simpleDateFormat.format(endTime);
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }
    
    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
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
    
    /**
     * Gets the output as text or <code>null</code> if it's not possible
     * to convert the output data into text.
     */
    public String getOutputAsText() {
        if (output == null) {
            return null;
        }
        if (output.startsWith("text:")) {
            return getOutput().substring(5);
        }
        if (output.startsWith("base64:")) {
            // TODO: Proper implementation:
            try {
                final byte[] result = DatatypeConverter.parseBase64Binary(output.substring(7));
                return new String(result, "UTF-8");
            } catch (Exception e) {
                return null;
            }
        }
        
        return null;
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
    
    public String getResponseHeadersAsString() {
        final StringBuilder sb = new StringBuilder();
        final List<String> keys = new ArrayList<String>(responseHeaders.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            for (String value : responseHeaders.get(key)) {
                sb.append(key + ": " + value + "\n");
            }
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return method + " " + path + "\nResponse HTTP-code: " + status + "\nClient Identifier: " + clientIdentifier;
    }
    
    public static List<HttpCall> readHttpCalls(String filename) {
        return new FileRecorderReader(filename).getRecordedHttp();
    }
}
