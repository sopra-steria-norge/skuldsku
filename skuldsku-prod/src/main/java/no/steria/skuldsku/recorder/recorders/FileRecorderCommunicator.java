package no.steria.skuldsku.recorder.recorders;

import java.io.FileOutputStream;
import java.io.IOException;

import no.steria.skuldsku.recorder.httprecorder.HttpCall;
import no.steria.skuldsku.recorder.httprecorder.HttpCallPersister;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaIntefaceCallPersister;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaInterfaceCall;

public class FileRecorderCommunicator implements HttpCallPersister, JavaIntefaceCallPersister  {

    private final StreamRecorderCommunicator src;
    
    public FileRecorderCommunicator(String filename) throws IOException {
        src = new StreamRecorderCommunicator(new FileOutputStream(filename));
    }
    
    @Override
    public void event(JavaInterfaceCall javaInterfaceCall) {
        src.event(javaInterfaceCall);
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
