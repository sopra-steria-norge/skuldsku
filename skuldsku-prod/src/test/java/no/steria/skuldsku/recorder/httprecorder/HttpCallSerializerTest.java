package no.steria.skuldsku.recorder.httprecorder;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class HttpCallSerializerTest {

    @Test
    public void shouldSerializeSingelElement() throws Exception {
        HttpCall httpCall = new HttpCall()
                .setMethod("GET").setPath("/mypath").setOutput("<html><body><h1>This is my output</h1></body></html>");

        String serialized = httpCall.serializedString();

        HttpCall copy = HttpCall.parseFromString(serialized);

        assertThat(copy).isNotNull();

        assertThat(copy.getMethod()).isEqualTo("GET");
        assertThat(copy.getParametersRead()).isEmpty();


    }
}
