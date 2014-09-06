package no.steria.skuldsku.example.basicservlet;

import java.util.List;

import no.steria.skuldsku.recorder.httprecorder.HttpCall;
import no.steria.skuldsku.recorder.recorders.FileRecorderReader;
import no.steria.skuldsku.testrunner.httprunner.HttpPlayer;

public class RunPlayback {

    public static void main(String[] args) throws Exception {
        final HttpPlayer testRunner = new HttpPlayer("http://localhost:8081");
        final List<HttpCall> httpCalls = new FileRecorderReader("data.txt").getRecordedHttp();
        testRunner.play(httpCalls);
    }
}
