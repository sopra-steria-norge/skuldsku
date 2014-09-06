package no.steria.skuldsku.example.basic.test;

import java.util.List;

import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaInterfaceCall;
import no.steria.skuldsku.testrunner.httprunner.HttpPlayer;
import no.steria.skuldsku.testrunner.interfacerunner.JavaInterfaceCallVerifier;
import no.steria.skuldsku.testrunner.interfacerunner.JavaInterfaceCallVerifierOptions;
import no.steria.skuldsku.testrunner.interfacerunner.JavaInterfaceVerifierResult;
import no.steria.skuldsku.testrunner.interfacerunner.verifiers.StrictJavaInterfaceCallVerifier;

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
        final JavaInterfaceVerifierResult result = compare(expectedData, actualData);
        
        System.out.println(result.getAdditionalInActual().size());
        System.out.println(result.getMissingFromActual().size());
        System.out.println(result.getNotEquals().size());
    }

    private static JavaInterfaceVerifierResult compare(final String expectedData, final String actualData) {
        final List<JavaInterfaceCall> expected = JavaInterfaceCall.readJavaInterfaceCalls(expectedData);
        final List<JavaInterfaceCall> actual = JavaInterfaceCall.readJavaInterfaceCalls(actualData);
        
        final JavaInterfaceCallVerifier verifier = new StrictJavaInterfaceCallVerifier();
        final JavaInterfaceVerifierResult result = verifier.assertEquals(expected, actual, new JavaInterfaceCallVerifierOptions());
        return result;
    }
}
