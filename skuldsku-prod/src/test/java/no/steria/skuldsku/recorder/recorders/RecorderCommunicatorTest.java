package no.steria.skuldsku.recorder.recorders;

import no.steria.skuldsku.recorder.httprecorder.HttpCall;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaInterfaceCall;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class RecorderCommunicatorTest {
    private static class InMemoryRecorderCommunicator extends AbstractRecorderCommunicator {
        private List<String> recorded = new ArrayList<>();

        @Override
        protected void saveRecord(String res) {
            recorded.add(res);
        }

        @Override
        protected List<String> getRecordedRecords() {
            return recorded;
        }
    }

    @Test
    public void shouldReturnRecordedHttpCalls() throws Exception {
        InMemoryRecorderCommunicator inMemoryRecorderCommunicator = new InMemoryRecorderCommunicator();

        HttpCall httpCall = new HttpCall()
                .setMethod("GET")
                .setPath("/path")
                .setReadInputStream("my input stream")
                ;

        inMemoryRecorderCommunicator.reportCall(httpCall);
        inMemoryRecorderCommunicator.event(new JavaInterfaceCall("", "class","method","para","result"));

        List<HttpCall> recordedHttpObjects = inMemoryRecorderCommunicator.getRecordedHttp();

        assertThat(recordedHttpObjects).hasSize(1);
        HttpCall fetched = recordedHttpObjects.get(0);

        assertThat(fetched.getMethod()).isEqualTo("GET");
        assertThat(fetched.getReadInputStream()).isEqualTo("my input stream");
    }
}
