package no.steria.skuldsku.recorder.recorders;

import java.util.ArrayList;
import java.util.List;

import no.steria.skuldsku.recorder.httprecorder.HttpCall;
import no.steria.skuldsku.recorder.httprecorder.HttpCallPersister;
import no.steria.skuldsku.recorder.httprecorder.SkuldskuFilter;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaIntefaceCallPersister;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaInterfaceCall;
import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;

public abstract class AbstractRecorderCommunicator implements HttpCallPersister, JavaIntefaceCallPersister {
    @Override
    public void initialize() {

    }

    @Override
    public void reportCall(HttpCall httpCall) {
        StringBuilder res = new StringBuilder();
        res.append("http%");
        res.append(SkuldskuFilter.getRequestId());
        res.append("%");
        ClassSerializer classSerializer = new ClassSerializer();
        res.append(classSerializer.asString(httpCall));
        saveRecord(res.toString());
    }

    protected abstract void saveRecord(String res);

    protected List<String> getRecordedRecords() {
        return new ArrayList<>();
    }

    public List<HttpCall> getRecordedHttp() {
        List<String> recordedRecords = getRecordedRecords();
        List<HttpCall> httpCalls = new ArrayList<>();
        for (String record : recordedRecords) {
            if (!record.startsWith("http%")) {
                continue;
            }
            ClassSerializer classSerializer = new ClassSerializer();
            int stpos = record.indexOf("%",5);
            HttpCall recordObject = (HttpCall) classSerializer.asObject(record.substring(stpos));
            httpCalls.add(recordObject);
        }
        return httpCalls;
    }
    
    public List<JavaInterfaceCall> getJavaInterfaceCalls() {
        List<String> recordedRecords = getRecordedRecords();
        List<JavaInterfaceCall> javaInterfaceCalls = new ArrayList<>();
        for (String record : recordedRecords) {
            if (!record.startsWith("inter%")) {
                continue;
            }
            ClassSerializer classSerializer = new ClassSerializer();
            int stpos = record.indexOf("%", 6);
            JavaInterfaceCall recordObject = (JavaInterfaceCall) classSerializer.asObject(record.substring(stpos));
            javaInterfaceCalls.add(recordObject);
        }
        return javaInterfaceCalls;
    }

    @Override
    public void event(JavaInterfaceCall javaInterfaceCall) {
        StringBuilder res = new StringBuilder();
        res.append("inter%");
        res.append(SkuldskuFilter.getRequestId());
        res.append("%");
        String asString = new ClassSerializer().asString(javaInterfaceCall);
        res.append(asString);

        saveRecord(res.toString());
    }
}
