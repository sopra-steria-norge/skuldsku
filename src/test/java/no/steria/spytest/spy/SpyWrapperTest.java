package no.steria.spytest.spy;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SpyWrapperTest {
    @Test
    public void shouldHandleBasic() throws Exception {
        ReportCallback reportCallback = mock(ReportCallback.class);

        when(reportCallback.doReport()).thenReturn(true);
        when(reportCallback.doReport(anyString(),anyString())).thenReturn(true);

        ServiceInterface serviceClass = SpyWrapper.newInstance(new ServiceClass(), ServiceInterface.class, reportCallback);

        String result = serviceClass.doSimpleService("MyName");

        assertThat(result).isEqualTo("Hello MyName");

        verify(reportCallback).doReport();
        verify(reportCallback).doReport("no.steria.spytest.spy.ServiceClass","doSimpleService");
        verify(reportCallback).event("no.steria.spytest.spy.ServiceClass","doSimpleService","<java.lang.String;MyName>","<java.lang.String;Hello MyName>");

    }


}
