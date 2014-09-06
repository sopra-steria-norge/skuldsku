package no.steria.skuldsku.testrunner.interfacerunner;

import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaInterfaceCall;

import java.util.List;

public class InterfaceRecordingChecker {

    public CompareResult compare(List<JavaInterfaceCall> expected, List<JavaInterfaceCall> actual) {
        boolean ok = !Pair.pairs(expected, actual).stream()
            .filter(pa -> !pa.getA().equals(pa.getB()))
            .findAny().isPresent();
        if (ok) {
            return CompareResult.ok();
        }
        return CompareResult.fail();
    }
}
