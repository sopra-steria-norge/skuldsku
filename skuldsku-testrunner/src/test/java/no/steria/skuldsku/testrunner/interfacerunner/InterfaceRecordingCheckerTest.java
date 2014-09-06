package no.steria.skuldsku.testrunner.interfacerunner;

import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaInterfaceCall;
import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;
import no.steria.skuldsku.testrunner.interfacerunner.verifiers.StrictJavaInterfaceCallVerifier;
import org.junit.Test;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;

public class InterfaceRecordingCheckerTest {

    private final JavaInterfaceCallVerifier verifier = new StrictJavaInterfaceCallVerifier();

    private static class Simple {
        private String value;
    }

    private static class Service {
        public Simple doIt(String val) {
            Simple simple = new Simple();
            simple.value = val;
            return simple;
        }

        public String reverseIt(Simple simple) {
            return simple.value;
        }
    }

    @Test
    public void shouldFindEquals()throws Exception {
        Simple simple = new Simple();
        simple.value = "result";
        ClassSerializer classSerializer = new ClassSerializer();
        String para = classSerializer.asString("para");
        JavaInterfaceCall a = new JavaInterfaceCall("Service", "doIt", para, classSerializer.asString(simple));
        JavaInterfaceCall b = new JavaInterfaceCall("Service", "doIt", para, classSerializer.asString(simple));

        final JavaInterfaceVerifierResult verifierResult = verifier.assertEquals(Arrays.asList(a), Arrays.asList(b), new JavaInterfaceCallVerifierOptions());
        assertThat(verifierResult.getNotEquals()).isEmpty();
    }

    @Test
    public void shouldFindNotEquals()throws Exception {
        Simple simple = new Simple();
        simple.value = "result";
        ClassSerializer classSerializer = new ClassSerializer();
        JavaInterfaceCall a = new JavaInterfaceCall("Service", "doIt", classSerializer.asString("parax"), classSerializer.asString(simple));
        JavaInterfaceCall b = new JavaInterfaceCall("Service", "doIt", classSerializer.asString("para"), classSerializer.asString(simple));

        final JavaInterfaceVerifierResult verifierResult = verifier.assertEquals(Arrays.asList(a), Arrays.asList(b), new JavaInterfaceCallVerifierOptions());

        assertThat(verifierResult.getNotEquals()).isNotEmpty();
    }



    @Test
    public void shouldRegisterMissingCall() throws Exception {
        Simple simple = new Simple();
        simple.value = "result";
        ClassSerializer classSerializer = new ClassSerializer();
        String para = classSerializer.asString("para");
        JavaInterfaceCall a = new JavaInterfaceCall("Service", "doIt", para, classSerializer.asString(simple));
        JavaInterfaceCall b = new JavaInterfaceCall("Service", "reverseIt", classSerializer.asString(simple),para);
        JavaInterfaceCall c = new JavaInterfaceCall("Service", "doIt", para, classSerializer.asString(simple));

        JavaInterfaceCall d = new JavaInterfaceCall("Service", "doIt", para, classSerializer.asString(simple));
        JavaInterfaceCall e = new JavaInterfaceCall("Service", "doIt", para, classSerializer.asString(simple));

        final JavaInterfaceVerifierResult verifierResult = verifier.assertEquals(Arrays.asList(a, b, c), Arrays.asList(d, e), new JavaInterfaceCallVerifierOptions());

        assertThat(verifierResult.getNotEquals()).isEmpty();
        assertThat(verifierResult.getMissingFromActual()).hasSize(1);

    }
}
