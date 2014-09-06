package no.steria.skuldsku.testrunner.interfacerunner;

import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaInterfaceCall;
import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;

import java.util.List;
import java.util.stream.Collectors;

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

    public CompareResult compareSerialized(List<String> expected, List<String> actual) {
        List<JavaInterfaceCall> exp = toOject(expected);
        List<JavaInterfaceCall> act = toOject(actual);

        return compare(exp,act);
    }

    private List<JavaInterfaceCall> toOject(List<String> list) {
        ClassSerializer classSerializer = new ClassSerializer();
        return list.stream()
            .map(s -> {
                String[] split = s.split("%");
                JavaInterfaceCall javaInterfaceCall = (JavaInterfaceCall) classSerializer.asObject(split[split.length-1]);
                return javaInterfaceCall;
            })
            .collect(Collectors.toList())
        ;

    }
}
