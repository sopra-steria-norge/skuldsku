package no.steria.skuldsku.recorder.recorders;

import no.steria.skuldsku.recorder.httprecorder.CallReporter;
import no.steria.skuldsku.recorder.httprecorder.ReportObject;
import no.steria.skuldsku.recorder.httprecorder.ServletFilter;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.ReportCallback;
import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;

import java.io.OutputStream;
import java.io.PrintStream;

public class StreamRecorder extends AbstractRecorder implements CallReporter, ReportCallback {
    private PrintStream out;

    public StreamRecorder(OutputStream os) {
        this.out = new PrintStream(os);
    }


    @Override
    protected void saveRecord(String res) {
        out.println(res);
        out.flush();
    }


}
