package no.steria.spytest.serializer;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class SerializerTest {

    private final ClassSerializer serializer = new ClassSerializer();

    @Test
    public void shouldReturnClassNameForEmptyClass() throws Exception {
        assertThat(serializer.asString(new EmptyClass())).isEqualTo("<no.steria.spytest.serializer.EmptyClass>");
    }

    @Test
    public void shouldHandleNull() throws Exception {
        assertThat(serializer.asString(null)).isEqualTo("<null>");
    }

    @Test
    public void shouldDeserialiseNull() throws Exception {
        assertThat(serializer.asObject(serializer.asString(null))).isNull();
    }

    @Test
    public void shouldReturnClass() throws Exception {
        EmptyClass emptyClass = (EmptyClass) serializer.asObject(serializer.asString(new EmptyClass()));
        assertThat(emptyClass.getClass()).isEqualTo(EmptyClass.class);
    }

    @Test
    public void shouldParseSimpleStringField() throws Exception {
        ClassWithSimpleFields simpleFields = new ClassWithSimpleFields().setStringval("pedro");

        assertThat(serializer.asString(simpleFields)).isEqualTo("<no.steria.spytest.serializer.ClassWithSimpleFields;stringval=pedro;intval=0>");

    }
}
