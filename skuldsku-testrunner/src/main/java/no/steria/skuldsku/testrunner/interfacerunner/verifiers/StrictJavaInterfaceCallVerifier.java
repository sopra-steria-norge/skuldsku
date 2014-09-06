package no.steria.skuldsku.testrunner.interfacerunner.verifiers;

import java.util.List;

import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaInterfaceCall;
import no.steria.skuldsku.testrunner.interfacerunner.JavaInterfaceCallVerifier;
import no.steria.skuldsku.testrunner.interfacerunner.JavaInterfaceCallVerifierOptions;
import no.steria.skuldsku.testrunner.interfacerunner.JavaInterfaceVerifierResult;

public class StrictJavaInterfaceCallVerifier implements JavaInterfaceCallVerifier {
    
    @Override
    public JavaInterfaceVerifierResult assertEquals(List<JavaInterfaceCall> expected, List<JavaInterfaceCall> actual, JavaInterfaceCallVerifierOptions options) {
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
                result.addNotEquals(exp, act);
                expnum++;
                actnum++;
                continue;
            }

            int nextMatchInExpected = nextExactMatchIn(act,expnum+1,expected);
            int nextMatchInActual = nextExactMatchIn(exp,actnum+1,actual);

            if (nextMatchInExpected != -1) {
                for (int i=expnum;i<nextMatchInExpected;i++) {
                    result.addMissingFromActual(expected.get(i));
                }
                expnum = nextMatchInExpected;
                continue;
            }
            if (nextMatchInActual != -1) {
                for (int i=actnum;i<nextMatchInActual;i++) {
                    result.addAdditionalInActual(actual.get(i));
                }
                actnum = nextMatchInActual;
                continue;
            }
            break;
        }

        for (int i=expnum;i<expected.size();i++) {
            result.addMissingFromActual(expected.get(i));
        }
        for (int i=actnum;i<actual.size();i++) {
            result.addAdditionalInActual(actual.get(i));
        }


        return result;

    }

    private static int nextExactMatchIn(JavaInterfaceCall match, int start, List<JavaInterfaceCall> list) {
        for (int i=start;i<list.size();i++) {
            JavaInterfaceCall javaInterfaceCall = list.get(i);
            if (match.equals(javaInterfaceCall)) {
                return i;
            }
        }
        return -1;
    }
}
