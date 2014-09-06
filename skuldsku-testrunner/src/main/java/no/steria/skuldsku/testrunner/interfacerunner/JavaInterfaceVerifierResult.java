package no.steria.skuldsku.testrunner.interfacerunner;

import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaInterfaceCall;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;

import java.util.ArrayList;
import java.util.List;

public class JavaInterfaceVerifierResult {
    private final List<JavaInterfaceCall> missingFromActual = new ArrayList<JavaInterfaceCall>();
    private final List<JavaInterfaceCall> additionalInActual = new ArrayList<JavaInterfaceCall>();
    private final List<Pair<JavaInterfaceCall>> notEquals = new ArrayList<Pair<JavaInterfaceCall>>();

    public static JavaInterfaceVerifierResult compare(List<JavaInterfaceCall> expected,List<JavaInterfaceCall> actual) {
        JavaInterfaceVerifierResult result = new JavaInterfaceVerifierResult();

        int expnum=0;
        int actnum=0;

        while (expnum < expected.size() && actnum < actual.size()) {
            JavaInterfaceCall exp = expected.get(expnum);
            JavaInterfaceCall act = actual.get(actnum);

            if (exp.equals(act)) {
                expnum++;
                actnum++;
                continue;
            }
            if (exp.getClassName().equals(act.getClassName()) && exp.getMethodname().equals(act.getMethodname())) {
                result.notEquals.add(new Pair<JavaInterfaceCall>(exp,act));
                expnum++;
                actnum++;
                continue;
            }

        }

        for (int i=expnum;i<expected.size();i++) {
            result.missingFromActual.add(expected.get(i));
        }
        for (int i=actnum;i<actual.size();i++) {
            result.additionalInActual.add(actual.get(i));
        }


        return result;
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
