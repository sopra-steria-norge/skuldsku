package no.steria.copito.recorder.httprecorder;

import java.io.FileNotFoundException;

/**
 * Whenever the ServletFilter processes a ServletRequest or a ServletResponse, it will create a ReportObject, and call
 * on a CallReporter to handle the actual recording of the data.
 */
public interface CallReporter {
    public void reportCall(ReportObject reportObject);

    /**
     * Each CallReporter implementation must have a means of returning all data collected a String, this
     * will be used by the RecorderFacade to print all results to a common file.
     */
    // TODO ikh: should rather return a stream?
    public String getRecordedData() throws FileNotFoundException;
}
