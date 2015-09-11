package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

import no.steria.skuldsku.recorder.recorders.FileRecorderReader;

import java.io.Serializable;
import java.util.List;

public class JavaInterfaceCall implements Serializable {
    private String clientIdentifier = "";
    private String className;
    private String methodname;
    private String parameters;
    private String result;

    public JavaInterfaceCall(String clientIdentifier, String className, String methodname, String parameters, String result) {
        this.clientIdentifier = clientIdentifier;
        this.className = className;
        this.methodname = methodname;
        this.parameters = parameters;
        this.result = result;
    }

    public JavaInterfaceCall() {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavaInterfaceCall that = (JavaInterfaceCall) o;

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

    public static List<JavaInterfaceCall> readJavaInterfaceCalls(String filename) {
        return new FileRecorderReader(filename).getJavaInterfaceCalls();
    }
}
