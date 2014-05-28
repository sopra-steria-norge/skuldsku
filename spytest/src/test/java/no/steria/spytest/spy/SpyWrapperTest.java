package no.steria.spytest.spy;

import no.steria.spytest.serializer.ClassSerializer;
import no.steria.spytest.serializer.ClassWithSimpleFields;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class SpyWrapperTest {

    private final DummyReportCallback reportCallback = new DummyReportCallback();
    private ServiceInterface serviceClass;

    @Before
    public void setUp() throws Exception {
        serviceClass = SpyWrapper.newInstance(new ServiceClass(), ServiceInterface.class, reportCallback, AsyncMode.ALL_SYNC);
    }

    @Test
    public void shouldHandleBasic() throws Exception {
        String result = serviceClass.doSimpleService("MyName");

        assertThat(result).isEqualTo("Hello MyName");

        assertThat(reportCallback.getClassName()).isEqualTo("no.steria.spytest.spy.ServiceClass");
        assertThat(reportCallback.getMethodname()).isEqualTo("doSimpleService");
        assertThat(reportCallback.getParameters()).isEqualTo("<java.lang.String;MyName>");
        assertThat(reportCallback.getResult()).isEqualTo("<java.lang.String;Hello MyName>");
    }

    @Test
    public void shouldHandleListsAsResults() throws Exception {
        List<String> result = serviceClass.returnList(new ClassWithSimpleFields().setIntval(42));

        assertThat(result).containsOnly("This","is","not","null");

        assertThat(reportCallback.getClassName()).isEqualTo("no.steria.spytest.spy.ServiceClass");
        assertThat(reportCallback.getMethodname()).isEqualTo("returnList");

        ClassSerializer classSerializer = new ClassSerializer();

        ClassWithSimpleFields simpleFields = (ClassWithSimpleFields) classSerializer.asObject(reportCallback.getParameters());

        assertThat(simpleFields).isNotNull();
        assertThat(simpleFields.getIntval()).isEqualTo(42);

       @SuppressWarnings("unchecked") List<String> recodredResult = (List<String>) classSerializer.asObject(reportCallback.getResult());
       assertThat(recodredResult).containsOnly("This","is","not","null");
    }


}
