package no.steria.copito.testrunner.httprunner;

import no.steria.copito.httprecorder.ReportObject;

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
