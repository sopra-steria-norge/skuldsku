package no.steria.skuldsku.example.basicservlet.recorder;

import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.ReportCallback;

public class SysoutRecorder implements ReportCallback {
    @Override
    public void event(String className, String methodname, String parameters,String result) {
        System.out.println(className + "%" + methodname + "%" + parameters + result);
    }

}
