package no.steria.skuldsku.testrunner.interfacerunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaInterfaceCall;

public class JavaInterfaceVerifierResult {
    private final List<JavaInterfaceCall> missingFromActual = new ArrayList<JavaInterfaceCall>();
    private final List<JavaInterfaceCall> additionalInActual = new ArrayList<JavaInterfaceCall>();
    private final List<Pair<JavaInterfaceCall>> notEquals = new ArrayList<Pair<JavaInterfaceCall>>();


    public JavaInterfaceVerifierResult() {
        
    }
    
    public JavaInterfaceVerifierResult(Collection<JavaInterfaceVerifierResult> results) {
        for (JavaInterfaceVerifierResult vr : results) {
            missingFromActual.addAll(vr.missingFromActual);
            additionalInActual.addAll(vr.additionalInActual);
            notEquals.addAll(vr.notEquals);
        }
    }
    
    
    public void addMissingFromActual(JavaInterfaceCall javaInterfaceCall) {
        missingFromActual.add(javaInterfaceCall);
    }
    
    public void addAdditionalInActual(JavaInterfaceCall javaInterfaceCall) {
        additionalInActual.add(javaInterfaceCall);
    }
    
    public void addNotEquals(JavaInterfaceCall expected, JavaInterfaceCall actual) {
        notEquals.add(new Pair<JavaInterfaceCall>(expected, actual));
    }
    
    public List<JavaInterfaceCall> getMissingFromActual() {
        return missingFromActual;
    }

    public List<JavaInterfaceCall> getAdditionalInActual() {
        return additionalInActual;
    }

    public List<Pair<JavaInterfaceCall>> getNotEquals() {
        return notEquals;
    }
}
