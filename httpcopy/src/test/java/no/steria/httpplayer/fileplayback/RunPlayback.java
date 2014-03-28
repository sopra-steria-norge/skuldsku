package no.steria.httpplayer.fileplayback;

import no.steria.httpspy.ReportObject;
import no.steria.reporter.FileCallReporter;

import java.util.List;

public class RunPlayback {
    public static void main(String[] args) throws Exception {
        List<ReportObject> reportObjects = FileCallReporter.readReportedObjects("/tmp/copytrack.txt");

        System.out.println("Read");

    }


}
