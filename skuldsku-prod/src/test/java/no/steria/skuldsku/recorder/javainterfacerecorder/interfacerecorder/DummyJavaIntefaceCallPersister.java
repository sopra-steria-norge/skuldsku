package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

public class DummyJavaIntefaceCallPersister implements JavaIntefaceCallPersister {
    private JavaInterfaceCall javaInterfaceCall;

    @Override
    public void event(JavaInterfaceCall javaInterfaceCall) {
        this.javaInterfaceCall = javaInterfaceCall;
    }

    public JavaInterfaceCall getJavaInterfaceCall() {
        return javaInterfaceCall;
    }
}
