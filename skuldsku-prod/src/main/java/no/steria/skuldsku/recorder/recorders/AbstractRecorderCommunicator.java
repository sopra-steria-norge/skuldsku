package no.steria.skuldsku.recorder.recorders;

import java.util.ArrayList;
import java.util.List;

import no.steria.skuldsku.recorder.http.HttpCall;
import no.steria.skuldsku.recorder.http.HttpCallPersister;
import no.steria.skuldsku.recorder.http.SkuldskuFilter;
import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.recorder.java.recorder.JavaCallPersister;
import no.steria.skuldsku.recorder.java.serializer.ClassSerializer;

public abstract class AbstractRecorderCommunicator implements HttpCallPersister, JavaCallPersister {
    @Override
    public void initialize() {

    }

    @Override
    public void reportCall(HttpCall httpCall) {
        StringBuilder res = new StringBuilder();
        res.append("http%");
        res.append("0"); // Unused
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
    
    public List<JavaCall> getJavaInterfaceCalls() {
        List<String> recordedRecords = getRecordedRecords();
        List<JavaCall> javaCalls = new ArrayList<>();
        for (String record : recordedRecords) {
            if (!record.startsWith("inter%")) {
                continue;
            }
            ClassSerializer classSerializer = new ClassSerializer();
            int stpos = record.indexOf("%", 6);
            JavaCall recordObject = (JavaCall) classSerializer.asObject(record.substring(stpos));
            javaCalls.add(recordObject);
        }
        return javaCalls;
    }

    @Override
    public void event(JavaCall javaCall) {
        StringBuilder res = new StringBuilder();
        res.append("inter%");
        res.append("0"); // Unused
        res.append("%");
        String asString = new ClassSerializer().asString(javaCall);
        res.append(asString);

        saveRecord(res.toString());
    }
}
