package no.steria.copito.testrunner.httpspy;

import no.steria.copito.testrunner.httpspy.reporter.FileCallReporter;

import java.util.List;

public class FileCallReporterReadback {
    public static void main(String[] args) {
        List<ReportObject> reportObjects = FileCallReporter.readReportedObjects("/tmp/copytrack.txt");
        System.out.println("Read");

    }
}
