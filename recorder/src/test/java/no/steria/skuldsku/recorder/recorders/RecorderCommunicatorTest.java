package no.steria.skuldsku.recorder.recorders;

import no.steria.skuldsku.recorder.httprecorder.ReportObject;
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

        ReportObject reportObject = new ReportObject()
                .setMethod("GET")
                .setPath("/path")
                .setReadInputStream("my input stream")
                ;

        inMemoryRecorderCommunicator.reportCall(reportObject);
        inMemoryRecorderCommunicator.event("class","method","para","result");

        List<ReportObject> recordedHttpObjects = inMemoryRecorderCommunicator.getRecordedHttp();

        assertThat(recordedHttpObjects).hasSize(1);
        ReportObject fetched = recordedHttpObjects.get(0);

        assertThat(fetched.getMethod()).isEqualTo("GET");
        assertThat(fetched.getReadInputStream()).isEqualTo("my input stream");
    }
}
