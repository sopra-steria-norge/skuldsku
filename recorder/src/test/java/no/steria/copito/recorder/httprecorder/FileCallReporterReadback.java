package no.steria.copito.recorder.httprecorder;


import no.steria.copito.recorder.httprecorder.reporter.FileCallReporter;

import java.util.List;

public class FileCallReporterReadback {
    public static void main(String[] args) {
        List<ReportObject> reportObjects = FileCallReporter.readReportedObjects("/tmp/copytrack.txt");
        System.out.println("Read");

    }
}
