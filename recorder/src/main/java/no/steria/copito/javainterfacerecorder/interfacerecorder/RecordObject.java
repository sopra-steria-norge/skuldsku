package no.steria.copito.javainterfacerecorder.interfacerecorder;

public class RecordObject {
    private final String serviceName;
    private final String method;
    private final String parameters;
    private final String result;

    public RecordObject(String serviceName, String method, String parameters, String result) {
        this.serviceName = serviceName;
        this.method = method;
        this.parameters = parameters;
        this.result = result;
    }
}
