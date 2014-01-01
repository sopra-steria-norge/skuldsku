package no.steria.spytest.serializer;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;

public class ValueEncodingDecodingTest {
    private void assertEncDec(Object obj) {
        ClassSerializer serializer = new ClassSerializer();
        assertThat(serializer.objectValueFromString(serializer.encodeValue(obj), obj.getClass())).isEqualTo(obj);
    }

    @Test
    public void shouldHandleBasicJavaLang() throws Exception {
        assertEncDec("abc");
        assertEncDec(34);
        assertEncDec(42L);
        assertEncDec('x');
        assertEncDec(3.14d);
        assertEncDec(new Date());
        assertEncDec(new DateTime());

    }
}
