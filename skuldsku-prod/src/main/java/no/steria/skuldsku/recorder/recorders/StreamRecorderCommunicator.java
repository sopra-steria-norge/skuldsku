package no.steria.skuldsku.recorder.recorders;

import no.steria.skuldsku.recorder.httprecorder.HttpCallPersister;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaIntefaceCallPersister;

import java.io.OutputStream;
import java.io.PrintStream;

public class StreamRecorderCommunicator extends AbstractRecorderCommunicator implements HttpCallPersister, JavaIntefaceCallPersister {
    private PrintStream out;

    public StreamRecorderCommunicator(OutputStream os) {
        if (os instanceof PrintStream) {
            this.out = (PrintStream) os;
        } else {
            this.out = new PrintStream(os);
        }
    }


    @Override
    protected void saveRecord(String res) {
        out.println(res);
        out.flush();
    }


}
