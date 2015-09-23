package no.steria.skuldsku.testrunner.interfacerunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import no.steria.skuldsku.recorder.java.JavaCall;

public class JavaCallVerifierResult {
    private final List<JavaCall> missingFromActual = new ArrayList<JavaCall>();
    private final List<JavaCall> additionalInActual = new ArrayList<JavaCall>();
    private final List<Pair<JavaCall>> notEquals = new ArrayList<Pair<JavaCall>>();


    public JavaCallVerifierResult() {
        
    }
    
    public JavaCallVerifierResult(Collection<JavaCallVerifierResult> results) {
        for (JavaCallVerifierResult vr : results) {
            missingFromActual.addAll(vr.missingFromActual);
            additionalInActual.addAll(vr.additionalInActual);
            notEquals.addAll(vr.notEquals);
        }
    }
    
    
    public void addMissingFromActual(JavaCall javaCall) {
        missingFromActual.add(javaCall);
    }
    
    public void addAdditionalInActual(JavaCall javaCall) {
        additionalInActual.add(javaCall);
    }
    
    public void addNotEquals(JavaCall expected, JavaCall actual) {
        notEquals.add(new Pair<JavaCall>(expected, actual));
    }
    
    public List<JavaCall> getMissingFromActual() {
        return missingFromActual;
    }

    public List<JavaCall> getAdditionalInActual() {
        return additionalInActual;
    }

    public List<Pair<JavaCall>> getNotEquals() {
        return notEquals;
    }
}
