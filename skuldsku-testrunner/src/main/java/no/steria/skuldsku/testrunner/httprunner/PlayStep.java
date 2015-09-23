package no.steria.skuldsku.testrunner.httprunner;

import no.steria.skuldsku.recorder.http.HttpCall;

public class PlayStep {
    private final HttpCall reportObject;
    private String recorded;

    public PlayStep(HttpCall reportObject) {
        this.reportObject = reportObject;
    }

    public HttpCall getReportObject() {
        return reportObject;
    }


    public void setRecorded(String output) {
        this.recorded = output;
    }




    public String getRecorded() {
        return recorded;
    }
}
