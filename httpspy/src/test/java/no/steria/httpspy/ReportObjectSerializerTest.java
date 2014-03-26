package no.steria.httpspy;

import org.fest.assertions.Assertions;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class ReportObjectSerializerTest {

    @Test
    public void shouldSerializeSingelElement() throws Exception {
        ReportObject reportObject = new ReportObject()
                .setMethod("GET").setPath("/mypath").setOutput("<html><body><h1>This is my output</h1></body></html>");

        String serialized = reportObject.serializedString();

        ReportObject copy = ReportObject.fromString(serialized);

        assertThat(copy).isNotNull();

        assertThat(copy.getMethod()).isEqualTo("GET");
        assertThat(copy.getParametersRead()).isEmpty();


    }
}
