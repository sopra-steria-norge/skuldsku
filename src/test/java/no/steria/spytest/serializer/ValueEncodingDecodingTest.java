package no.steria.spytest.serializer;

import org.joda.time.DateTime;
import org.junit.Test;

import java.lang.reflect.Array;
import java.math.BigDecimal;
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
        assertEncDec(new BigDecimal(3.18d));
    }


    @Test
    public void shouldHandleArrays() throws Exception {
        String arr[]={"a","b","c"};
        ClassSerializer serializer = new ClassSerializer();
        String encodeValue = serializer.encodeValue(arr);
        assertThat(encodeValue).isEqualTo("<array;a;b;c>");
        String[] clonedArr = (String[]) serializer.objectValueFromString(encodeValue,arr.getClass());

        assertThat(clonedArr).containsOnly("a","b","c");
    }

    @Test
    public void fun() throws Exception {
        Object obj= Array.newInstance(String.class,1);
        Array.set(obj,0,"Abc");

    }
}
