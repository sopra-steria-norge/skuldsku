package no.steria.skuldsku.testrunner;

import java.util.List;

import no.steria.skuldsku.recorder.http.HttpCall;
import no.steria.skuldsku.recorder.recorders.FileRecorderReader;
import no.steria.skuldsku.testrunner.httprunner.HttpPlayer;

public final class HttpTestRunner {

    private final String url;
    private final String testInputFilename;
    
    
    public HttpTestRunner(String url, String testInputFilename) {
        this.url = url;
        this.testInputFilename = testInputFilename;
    }
    
    
    public void execute() {
        final FileRecorderReader fileRecorder = new FileRecorderReader(testInputFilename);
        final List<HttpCall> recordedHttp = fileRecorder.getRecordedHttp();
        
        final HttpPlayer httpPlayer = new HttpPlayer(url);
        httpPlayer.play(recordedHttp);
    }
}
