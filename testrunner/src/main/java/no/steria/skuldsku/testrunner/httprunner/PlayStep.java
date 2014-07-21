package no.steria.skuldsku.testrunner.httprunner;

import no.steria.skuldsku.recorder.httprecorder.ReportObject;

public class PlayStep {
    private ReportObject reportObject;
    private String recorded;

    public PlayStep(ReportObject reportObject) {
        this.reportObject = reportObject;
    }

    public ReportObject getReportObject() {
        return reportObject;
    }


    public void record(String output) {
        this.recorded = output;
    }




    public String getRecorded() {
        return recorded;
    }
}
