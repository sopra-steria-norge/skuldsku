package no.steria.skuldsku.recorder.recorders;

import no.steria.skuldsku.recorder.http.HttpCallPersister;
import no.steria.skuldsku.recorder.java.recorder.JavaCallPersister;

import java.io.OutputStream;
import java.io.PrintStream;

public class StreamRecorderCommunicator extends AbstractRecorderCommunicator implements HttpCallPersister, JavaCallPersister {
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
