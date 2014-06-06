package no.steria.copito.recorder.javainterfacerecorder.serializer;

import org.joda.time.DateTime;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class ValueEncodingDecodingTest {

    private final ClassSerializer serializer = new ClassSerializer();

    private void assertEncDec(Object obj) {
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
        assertEncDec(new BigDecimal(3.18d));
    }


    @Test
    public void shouldHandleArrays() throws Exception {
        String arr[]={"a","b","c"};
        String encodeValue = serializer.encodeValue(arr);
        assertThat(encodeValue).isEqualTo("<array;<java.lang.String;a>;<java.lang.String;b>;<java.lang.String;c>>");
        String[] clonedArr = (String[]) serializer.objectValueFromString(encodeValue,arr.getClass());

        assertThat(clonedArr).containsOnly("a","b","c");
    }

    @Test
    public void shouldHandleList() throws Exception {
        List<Integer> primes= Arrays.asList(1,2,3,5,7,11);
        String serializedVal = serializer.encodeValue(primes);
        assertThat(serializedVal).isEqualTo("<list;<java.lang.Integer;1>;<java.lang.Integer;2>;<java.lang.Integer;3>;<java.lang.Integer;5>;<java.lang.Integer;7>;<java.lang.Integer;11>>");

        @SuppressWarnings("unchecked") List<Integer> clonedPrimes= (List<Integer>) serializer.objectValueFromString(serializedVal,Integer.class);

        assertThat(clonedPrimes).isEqualTo(primes);

    }
}
