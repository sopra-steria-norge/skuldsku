package no.steria.skuldsku.example.basic.test;

import java.util.List;

import no.steria.skuldsku.common.result.Result;
import no.steria.skuldsku.common.result.Results;
import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.testrunner.httprunner.HttpPlayer;
import no.steria.skuldsku.testrunner.interfacerunner.JavaCallVerifier;
import no.steria.skuldsku.testrunner.interfacerunner.JavaCallVerifierOptions;
import no.steria.skuldsku.testrunner.interfacerunner.verifiers.StrictJavaCallVerifier;

public class RunPlayback {

    public static void main(String[] args) throws Exception {
        final String expectedData = "data.txt";
        
        final HttpPlayer testRunner = new HttpPlayer("http://localhost:8081");
        testRunner.play(expectedData);
        
        /*
         * Retrive actual data. In our example, we just reference it directly.
         */
        final String actualData = "../example-basic-application/data.txt";
        
        verifyResult(expectedData, actualData);
    }

    private static void verifyResult(final String expectedData, final String actualData) {
        final Results results = compare(expectedData, actualData);
        
        for (Result r : results) {
            System.out.println(r.toString());
        }
    }

    private static Results compare(final String expectedData, final String actualData) {
        final List<JavaCall> expected = JavaCall.readJavaInterfaceCalls(expectedData);
        final List<JavaCall> actual = JavaCall.readJavaInterfaceCalls(actualData);
        
        final JavaCallVerifier verifier = new StrictJavaCallVerifier();
        final Results results = verifier.assertEquals(expected, actual, new JavaCallVerifierOptions());
        return results;
    }
}
