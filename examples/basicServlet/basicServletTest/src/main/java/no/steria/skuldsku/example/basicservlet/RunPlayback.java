package no.steria.skuldsku.example.basicservlet;

import no.steria.skuldsku.testrunner.HttpTestRunner;

public class RunPlayback {

    public static void main(String[] args) throws Exception {
        final HttpTestRunner testRunner = new HttpTestRunner("http://localhost:8081", "/tmp/runit.txt");
        testRunner.execute();
    }
}
