package no.steria.httpspy;

import java.util.HashMap;
import java.util.Map;

public class ReportObject {
    private String readInputStream;
    private Map<String,String> parameters = new HashMap<>();

    public void setReadInputStream(String readInputStream) {
        this.readInputStream = readInputStream;
    }

    public String getReadInputStream() {
        return readInputStream;
    }

    public Map<String,String> getParametersRead() {
        return parameters;
    }
}
