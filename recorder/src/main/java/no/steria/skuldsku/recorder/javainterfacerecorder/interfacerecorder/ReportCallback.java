package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

public interface ReportCallback {
    public void event(String className, String methodname, String parameters,String result);
}
