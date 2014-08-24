package no.steria.skuldsku.testrunner.httprunner.fileplayback;

import java.util.List;

import no.steria.skuldsku.recorder.httprecorder.ReportObject;
import no.steria.skuldsku.recorder.httprecorder.reporter.FileCallReporter;
import no.steria.skuldsku.testrunner.httprunner.HttpPlayer;

public class RunPlayback {
    public static void main(String[] args) throws Exception {
        List<ReportObject> reportObjects = FileCallReporter.readReportedObjects("/tmp/trcpy.txt");

        System.out.println("Read");

        HttpPlayer httpPlayer = new HttpPlayer("http://localhost:21090/someother");
        httpPlayer.play(reportObjects);
    }


}
