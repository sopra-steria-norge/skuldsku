package no.steria.skuldsku.testrunner.httprunner.fileplayback;

import no.steria.skuldsku.recorder.httprecorder.ReportObject;
import no.steria.skuldsku.recorder.httprecorder.reporter.FileCallReporter;
import no.steria.skuldsku.testrunner.httprunner.HttpPlayer;
import no.steria.skuldsku.testrunner.httprunner.PlayStep;


import java.util.List;
import java.util.stream.Collectors;

public class RunPlayback {
    public static void main(String[] args) throws Exception {
        List<ReportObject> reportObjects = FileCallReporter.readReportedObjects("/tmp/trcpy.txt");

        System.out.println("Read");

        HttpPlayer httpPlayer = new HttpPlayer("http://localhost:21090/someother");
        httpPlayer.play(reportObjects.stream().map(ro -> new PlayStep(ro)).collect(Collectors.toList()));
    }


}
