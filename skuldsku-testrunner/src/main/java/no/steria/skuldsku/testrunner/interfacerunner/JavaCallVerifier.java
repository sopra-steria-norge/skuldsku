package no.steria.skuldsku.testrunner.interfacerunner;

import java.util.List;

import no.steria.skuldsku.recorder.java.JavaCall;

public interface JavaCallVerifier {

    public JavaCallVerifierResult assertEquals(List<JavaCall> expected, List<JavaCall> actual, JavaCallVerifierOptions options);
    
}
