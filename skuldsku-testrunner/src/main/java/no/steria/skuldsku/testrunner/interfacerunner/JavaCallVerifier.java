package no.steria.skuldsku.testrunner.interfacerunner;

import java.util.List;

import no.steria.skuldsku.common.result.Results;
import no.steria.skuldsku.recorder.java.JavaCall;

public interface JavaCallVerifier {

    public Results assertEquals(List<JavaCall> expected, List<JavaCall> actual, JavaCallVerifierOptions options);
    
}
