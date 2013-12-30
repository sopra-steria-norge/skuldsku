package no.steria.spytest.serializer;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class SerializerTest {

    private final ClassSerializer serializer = new ClassSerializer();

    @Test
    public void shouldReturnClassNameForEmptyClass() throws Exception {
        assertThat(serializer.asString(new EmptyClass())).isEqualTo("<no.steria.spytest.    serializer.EmptyClass>");
    }

    @Test
    public void shouldHandleNull() throws Exception {
        assertThat(serializer.asString(null)).isEqualTo("<null>");

    }
}
