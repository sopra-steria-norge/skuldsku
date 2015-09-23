package no.steria.skuldsku.recorder.java.recorder;

import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.recorder.java.recorder.JavaCallPersister;

public class DummyJavaIntefaceCallPersister implements JavaCallPersister {
    private JavaCall javaCall;

    @Override
    public void event(JavaCall javaCall) {
        this.javaCall = javaCall;
    }

    public JavaCall getJavaInterfaceCall() {
        return javaCall;
    }
}
