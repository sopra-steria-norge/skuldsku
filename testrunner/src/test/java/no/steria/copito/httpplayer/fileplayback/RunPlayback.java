package no.steria.copito.httpplayer.fileplayback;

import no.steria.copito.httpplayer.HttpPlayer;
import no.steria.copito.httpplayer.PlayStep;
import no.steria.copito.httpspy.ReportObject;
import no.steria.copito.httpspy.reporter.FileCallReporter;

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