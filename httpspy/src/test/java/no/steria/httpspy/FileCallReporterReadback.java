package no.steria.httpspy;

import no.steria.reporter.FileCallReporter;

import java.util.List;

public class FileCallReporterReadback {
    public static void main(String[] args) {
        List<ReportObject> reportObjects = FileCallReporter.readReportedObjects("/tmp/copytrack.txt");
        System.out.println("Read");

    }
}