package no.steria.skuldsku.recorder.recorders;

import no.steria.skuldsku.recorder.httprecorder.CallReporter;
import no.steria.skuldsku.recorder.httprecorder.ReportObject;
import no.steria.skuldsku.recorder.httprecorder.ServletFilter;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.ReportCallback;
import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRecorderCommunicator implements CallReporter, ReportCallback {
    @Override
    public void initialize() {

    }

    @Override
    public void reportCall(ReportObject reportObject) {
        StringBuilder res = new StringBuilder();
        res.append("http%");
        res.append(ServletFilter.getRequestId());
        res.append("%");
        ClassSerializer classSerializer = new ClassSerializer();
        res.append(classSerializer.asString(reportObject));
        saveRecord(res.toString());
    }

    protected abstract void saveRecord(String res);

    protected List<String> getRecordedRecords() {
        return new ArrayList<>();
    }

    public List<ReportObject> getRecordedHttp() {
        List<String> recordedRecords = getRecordedRecords();
        List<ReportObject> reportObjects = new ArrayList<>();
        for (String record : recordedRecords) {
            if (!record.startsWith("http%")) {
                continue;
            }
            ClassSerializer classSerializer = new ClassSerializer();
            int stpos = record.indexOf("%",5);
            ReportObject recordObject = (ReportObject) classSerializer.asObject(record.substring(stpos));
            reportObjects.add(recordObject);
        }
        return reportObjects;
    }

    @Override
    public void event(String className, String methodname, String parameters, String result) {
        StringBuilder res = new StringBuilder();
        res.append("inter%");
        res.append(ServletFilter.getRequestId());
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
