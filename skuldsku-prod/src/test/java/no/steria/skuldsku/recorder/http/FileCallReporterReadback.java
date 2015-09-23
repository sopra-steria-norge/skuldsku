package no.steria.skuldsku.recorder.http;


import no.steria.skuldsku.recorder.http.HttpCall;
import no.steria.skuldsku.recorder.http.reporter.FileHttpCallPersister;

import java.util.List;

public class FileCallReporterReadback {
    public static void main(String[] args) {
        List<HttpCall> httpCalls = FileHttpCallPersister.readReportedObjects("/tmp/copytrack.txt");
        System.out.println("Read");

    }
}
