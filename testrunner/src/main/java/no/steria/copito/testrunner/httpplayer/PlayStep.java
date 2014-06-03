package no.steria.copito.testrunner.httpplayer;

import no.steria.copito.testrunner.httpspy.ReportObject;

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
