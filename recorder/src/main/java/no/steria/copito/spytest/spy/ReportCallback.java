package no.steria.copito.spytest.spy;

public interface ReportCallback {
    public boolean doReport();
    public boolean doReport(String className, String methodname);
    public void event(String className, String methodname, String parameters,String result);
}
