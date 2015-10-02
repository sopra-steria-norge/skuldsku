package no.steria.skuldsku.testrunner.interfacerunner.verifiers;

import java.util.List;

import no.steria.skuldsku.common.result.Results;
import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.testrunner.interfacerunner.JavaCallVerifier;
import no.steria.skuldsku.testrunner.interfacerunner.JavaCallVerifierOptions;
import no.steria.skuldsku.testrunner.interfacerunner.result.JavaCallAdditionalInActualResult;
import no.steria.skuldsku.testrunner.interfacerunner.result.JavaCallMissingFromActualResult;
import no.steria.skuldsku.testrunner.interfacerunner.result.JavaCallNotEqualsResult;

public class StrictJavaCallVerifier implements JavaCallVerifier {
    
    @Override
    public Results assertEquals(List<JavaCall> expected, List<JavaCall> actual, JavaCallVerifierOptions options) {
        Results results = new Results();

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
                results.addResult(new JavaCallNotEqualsResult(exp, act));
                expnum++;
                actnum++;
                continue;
            }

            int nextMatchInExpected = nextExactMatchIn(act,expnum+1,expected);
            int nextMatchInActual = nextExactMatchIn(exp,actnum+1,actual);

            if (nextMatchInExpected != -1) {
                for (int i=expnum;i<nextMatchInExpected;i++) {
                    results.addResult(new JavaCallMissingFromActualResult(expected.get(i)));
                }
                expnum = nextMatchInExpected;
                continue;
            }
            if (nextMatchInActual != -1) {
                for (int i=actnum;i<nextMatchInActual;i++) {
                    results.addResult(new JavaCallAdditionalInActualResult(actual.get(i)));
                }
                actnum = nextMatchInActual;
                continue;
            }
            break;
        }

        for (int i=expnum;i<expected.size();i++) {
            results.addResult(new JavaCallMissingFromActualResult(expected.get(i)));
        }
        for (int i=actnum;i<actual.size();i++) {
            results.addResult(new JavaCallAdditionalInActualResult(actual.get(i)));
        }


        return results;

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
