package no.steria.skuldsku.testrunner.interfacerunner.verifiers;

import java.util.List;

import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.testrunner.interfacerunner.JavaCallVerifier;
import no.steria.skuldsku.testrunner.interfacerunner.JavaCallVerifierOptions;
import no.steria.skuldsku.testrunner.interfacerunner.JavaCallVerifierResult;

public class StrictJavaCallVerifier implements JavaCallVerifier {
    
    @Override
    public JavaCallVerifierResult assertEquals(List<JavaCall> expected, List<JavaCall> actual, JavaCallVerifierOptions options) {
        JavaCallVerifierResult result = new JavaCallVerifierResult();

        int expnum=0;
        int actnum=0;

        while (expnum < expected.size() && actnum < actual.size()) {
            JavaCall exp = expected.get(expnum);
            JavaCall act = actual.get(actnum);

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

    private static int nextExactMatchIn(JavaCall match, int start, List<JavaCall> list) {
        for (int i=start;i<list.size();i++) {
            JavaCall javaCall = list.get(i);
            if (match.equals(javaCall)) {
                return i;
            }
        }
        return -1;
    }
}
