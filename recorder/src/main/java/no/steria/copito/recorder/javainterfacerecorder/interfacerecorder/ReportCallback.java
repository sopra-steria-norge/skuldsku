package no.steria.copito.recorder.javainterfacerecorder.interfacerecorder;

import java.io.IOException;
import java.io.OutputStreamWriter;

public interface ReportCallback {
    public boolean doReport();
    public boolean doReport(String className, String methodname);
    public void event(String className, String methodname, String parameters,String result);

    /**
     * Each CallReporter implementation must have a means of returning all data collected a String, this
     * will be used by the RecorderFacade to print all results to a common file.
     */
    public void writeRecordedDataTo(OutputStreamWriter outputStreamWriter) throws IOException;
}
