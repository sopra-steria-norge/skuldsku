package no.steria.copito.httprecorder;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ReportObjectSerializerTest {

    @Test
    public void shouldSerializeSingelElement() throws Exception {
        ReportObject reportObject = new ReportObject()
                .setMethod("GET").setPath("/mypath").setOutput("<html><body><h1>This is my output</h1></body></html>");

        String serialized = reportObject.serializedString();

        ReportObject copy = ReportObject.parseFromString(serialized);

        assertThat(copy).isNotNull();

        assertThat(copy.getMethod()).isEqualTo("GET");
        assertThat(copy.getParametersRead()).isEmpty();


    }
}
