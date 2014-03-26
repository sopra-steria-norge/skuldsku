package no.steria.httpspy;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ReportObject implements Serializable {
    private String readInputStream;
    private Map<String,String> parameters = new HashMap<>();
    private String method;
    private String path;
    private String output;

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
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(os)) {
            objectOutputStream.writeObject(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return os.toString();
    }

    public static ReportObject fromString(String serializedStr) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(serializedStr.getBytes()));
            return (ReportObject) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
