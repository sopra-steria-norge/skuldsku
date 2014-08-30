package no.steria.skuldsku.example.basicservlet;

import no.steria.skuldsku.testrunner.httprunner.HttpPlayer;

public class RunPlayback {

    public static void main(String[] args) throws Exception {
        final HttpPlayer testRunner = new HttpPlayer("http://localhost:8081");
    }
}
