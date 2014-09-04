package no.steria.skuldsku.testrunner.interfacerunner;

public class RecordedInterfaceCall {
    private String className;
    private String methodname;
    private String parameters;
    private String result;

    public RecordedInterfaceCall(String className, String methodname, String parameters, String result) {
        this.className = className;
        this.methodname = methodname;
        this.parameters = parameters;
        this.result = result;
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
}
