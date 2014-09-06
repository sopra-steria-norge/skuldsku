package no.steria.skuldsku.testrunner.interfacerunner;

import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaInterfaceCall;
import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;
import org.junit.Test;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;

public class InterfaceRecordingCheckerTest {
    private static class Simple {
        private String value;
    }

    private static class Service {
        public Simple doIt(String val) {
            Simple simple = new Simple();
            simple.value = val;
            return simple;
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

        InterfaceRecordingChecker interfaceRecordingChecker = new InterfaceRecordingChecker();
        CompareResult compareResult = interfaceRecordingChecker.compare(Arrays.asList(a), Arrays.asList(b));
        assertThat(compareResult.isEqual()).isTrue();

    }

    @Test
    public void shouldFindNotEquals()throws Exception {
        Simple simple = new Simple();
        simple.value = "result";
        ClassSerializer classSerializer = new ClassSerializer();
        JavaInterfaceCall a = new JavaInterfaceCall("Service", "doIt", classSerializer.asString("parax"), classSerializer.asString(simple));
        JavaInterfaceCall b = new JavaInterfaceCall("Service", "doIt", classSerializer.asString("para"), classSerializer.asString(simple));

        InterfaceRecordingChecker interfaceRecordingChecker = new InterfaceRecordingChecker();
        CompareResult compareResult = interfaceRecordingChecker.compare(Arrays.asList(a), Arrays.asList(b));
        assertThat(compareResult.isEqual()).isFalse();

    }
}
