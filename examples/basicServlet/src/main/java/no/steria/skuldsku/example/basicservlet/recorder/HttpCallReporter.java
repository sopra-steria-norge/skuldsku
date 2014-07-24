package no.steria.skuldsku.example.basicservlet.recorder;

import no.steria.skuldsku.recorder.httprecorder.CallReporter;
import no.steria.skuldsku.recorder.httprecorder.ReportObject;
import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;

public class HttpCallReporter implements CallReporter {
    @Override
    public void initialize() {

    }

    @Override
    public void reportCall(ReportObject reportObject) {
        ClassSerializer classSerializer = new ClassSerializer();
        System.out.println(classSerializer.asString(reportObject));
    }
}
