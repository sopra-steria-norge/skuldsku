package no.steria.skuldsku.example.basicservlet;

import java.util.List;

import no.steria.skuldsku.recorder.httprecorder.ReportObject;
import no.steria.skuldsku.recorder.recorders.FileRecorderReader;
import no.steria.skuldsku.testrunner.httprunner.HttpPlayer;

public class RunPlayback {

    public static void main(String[] args) throws Exception {
        final FileRecorderReader fileRecorder = new FileRecorderReader("/tmp/runit.txt");
        final List<ReportObject> recordedHttp = fileRecorder.getRecordedHttp();

        final HttpPlayer httpPlayer = new HttpPlayer("http://localhost:8081");

        httpPlayer.play(recordedHttp);

    }
}
