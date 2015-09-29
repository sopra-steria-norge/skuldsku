package no.steria.skuldsku.recorder.java;

import no.steria.skuldsku.recorder.recorders.FileRecorderReader;

import java.io.Serializable;
import java.util.List;

public class JavaCall implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String clientIdentifier = "";
    private String className;
    private String methodname;
    private String parameters;
    private String result;
    private String thrown;

    public JavaCall(String clientIdentifier, String className, String methodname, String parameters, String result, String thrown) {
        this.clientIdentifier = clientIdentifier;
        this.className = className;
        this.methodname = methodname;
        this.parameters = parameters;
        this.result = result;
        this.thrown = thrown;
    }

    public JavaCall() {

    }

    public String getClientIdentifier() {
        return clientIdentifier;
    }
    
    public void setClientIdentifier(String clientIdentifier) {
        this.clientIdentifier = (clientIdentifier != null) ? clientIdentifier : "";
    }
    
    public String getClassName() {
        return className;
    }

    public String getMethodname() {
        return methodname;
    }

    public String getParameters() {
        return parameters;
    }

    public String getResult() {
        return result;
    }
    
    public String getThrown() {
        return thrown;
    }
    
    public void setThrown(String thrown) {
        this.thrown = thrown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavaCall that = (JavaCall) o;

        if (!className.equals(that.className)) return false;
        if (!methodname.equals(that.methodname)) return false;
        if (!parameters.equals(that.parameters)) return false;
        if (!result.equals(that.result)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result1 = className.hashCode();
        result1 = 31 * result1 + methodname.hashCode();
        result1 = 31 * result1 + parameters.hashCode();
        result1 = 31 * result1 + result.hashCode();
        return result1;
    }

    public static List<JavaCall> readJavaInterfaceCalls(String filename) {
        return new FileRecorderReader(filename).getJavaInterfaceCalls();
    }
}
