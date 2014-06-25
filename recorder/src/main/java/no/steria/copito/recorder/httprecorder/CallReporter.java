package no.steria.copito.recorder.httprecorder;

/**
 * Whenever the ServletFilter processes a ServletRequest or a ServletResponse, it will create a ReportObject, and call
 * on a CallReporter to handle the actual recording of the data.
 */
public interface CallReporter {
    public void reportCall(ReportObject reportObject);
}
