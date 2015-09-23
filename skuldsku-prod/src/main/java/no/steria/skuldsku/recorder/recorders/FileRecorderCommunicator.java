package no.steria.skuldsku.recorder.recorders;

import java.io.FileOutputStream;
import java.io.IOException;

import no.steria.skuldsku.recorder.http.HttpCall;
import no.steria.skuldsku.recorder.http.HttpCallPersister;
import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.recorder.java.recorder.JavaCallPersister;

public class FileRecorderCommunicator implements HttpCallPersister, JavaCallPersister  {

    private final StreamRecorderCommunicator src;
    
    public FileRecorderCommunicator(String filename) throws IOException {
        src = new StreamRecorderCommunicator(new FileOutputStream(filename));
    }
    
    @Override
    public void event(JavaCall javaCall) {
        src.event(javaCall);
    }

    @Override
    public void initialize() {
        src.initialize();
    }

    @Override
    public void reportCall(HttpCall httpCall) {
        src.reportCall(httpCall);
    }

}
