package no.steria.skuldsku.example.basicservlet;

import no.steria.skuldsku.recorder.httprecorder.ReportObject;
import no.steria.skuldsku.recorder.recorders.FileRecorderReader;
import no.steria.skuldsku.testrunner.httprunner.HttpPlayer;
import no.steria.skuldsku.testrunner.httprunner.PlayStep;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RunPlayback {

    public static void main(String[] args) throws Exception {
        FileRecorderReader fileRecorder = new FileRecorderReader("/tmp/runit.txt");
        List<ReportObject> recordedHttp = fileRecorder.getRecordedHttp();

        List<PlayStep> playBook = new ArrayList<>();
        for (ReportObject reportObject : recordedHttp) {
            playBook.add(new PlayStep(reportObject));
        }

        HttpPlayer httpPlayer = new HttpPlayer("http://localhost:8081");

        httpPlayer.play(playBook);

    }
}
