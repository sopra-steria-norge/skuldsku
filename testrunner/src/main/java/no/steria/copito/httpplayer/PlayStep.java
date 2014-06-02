package no.steria.copito.httpplayer;

import no.steria.httpspy.ReportObject;

import java.util.HashMap;
import java.util.Map;

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
