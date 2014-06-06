package no.steria.copito.recorder.javainterfacerecorder.interfacerecorder;

public interface ReportCallback {
    public boolean doReport();
    public boolean doReport(String className, String methodname);
    public void event(String className, String methodname, String parameters,String result);
}
