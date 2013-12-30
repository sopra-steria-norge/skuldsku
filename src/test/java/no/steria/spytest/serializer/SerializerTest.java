package no.steria.spytest.serializer;

import org.fest.assertions.Assertions;
import org.junit.Test;

public class SerializerTest {
    @Test
    public void shouldReturnClassNameForEmptyClass() throws Exception {
        ClassSerializer serializer = new ClassSerializer();
        Assertions.assertThat(serializer.asString(new EmptyClass())).isEqualTo("<no.steria.spytest.serializer.EmptyClass>");

    }
}
