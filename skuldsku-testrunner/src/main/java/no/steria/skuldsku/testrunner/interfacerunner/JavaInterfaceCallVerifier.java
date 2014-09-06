package no.steria.skuldsku.testrunner.interfacerunner;

import java.util.List;

import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaInterfaceCall;

public interface JavaInterfaceCallVerifier {

    public JavaInterfaceVerifierResult assertEquals(List<JavaInterfaceCall> expected, List<JavaInterfaceCall> actual, JavaInterfaceCallVerifierOptions options);
    
}
