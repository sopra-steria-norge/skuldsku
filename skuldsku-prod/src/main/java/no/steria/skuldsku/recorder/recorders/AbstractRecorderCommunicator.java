package no.steria.skuldsku.recorder.recorders;

import no.steria.skuldsku.recorder.httprecorder.HttpCallPersister;
import no.steria.skuldsku.recorder.httprecorder.HttpCall;
import no.steria.skuldsku.recorder.httprecorder.SkuldskuFilter;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaIntefaceCallPersister;
import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void event(String className, String methodname, String parameters, String result) {
        StringBuilder res = new StringBuilder();
        res.append("inter%");
        res.append(SkuldskuFilter.getRequestId());
        res.append("%");
        res.append(className);
        res.append("%");
        res.append(methodname);
        res.append("%");
        res.append(parameters);
        res.append(result);
        saveRecord(res.toString());
    }
}
