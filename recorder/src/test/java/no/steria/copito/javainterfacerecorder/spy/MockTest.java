package no.steria.copito.javainterfacerecorder.spy;

import no.steria.copito.javainterfacerecorder.serializer.ClassSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class MockTest {
    private final DummyReportCallback reportCallback = new DummyReportCallback();


    @Before
    public void setUp() throws Exception {
        System.setProperty("no.steria.copito.doMock","true");
    }

    @Test
    public void shouldUseMock() throws Exception {
        MockRegistration.registerMock(ServiceInterface.class,MockFromServiceImpl.create(ServiceInterface.class,new ServiceMock()));
        ServiceInterface serviceClass = SpyWrapper.newInstance(new ServiceClass(), ServiceInterface.class, reportCallback, SpyConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());

        assertThat(serviceClass.doSimpleService("hoi")).isEqualTo("I am the mock hoi");

    }

    @Test
    public void recordMockShouldReturnNullWhenNoData() throws Exception {
        List<RecordObject> recorded = new ArrayList<>();
        RecordedDataMock recordedDataMock = new RecordedDataMock(recorded);

        MockRegistration.registerMock(ServiceInterface.class,recordedDataMock);
        ServiceInterface serviceClass = SpyWrapper.newInstance(new ServiceClass(), ServiceInterface.class, reportCallback, SpyConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());

        assertThat(serviceClass.doSimpleService("hoi")).isNull();

    }

    @Test
    public void recordMockShouldReturnGivenData() throws Exception {
        ClassSerializer serializer = new ClassSerializer();
        List<RecordObject> recorded = new ArrayList<>();

        RecordObject recordObject = new RecordObject(ServiceClass.class.getName(), "doSimpleService", serializer.asString("hoi"), serializer.asString("This is fixed"));
        recorded.add(recordObject);

        RecordedDataMock recordedDataMock = new RecordedDataMock(recorded);

        MockRegistration.registerMock(ServiceInterface.class,recordedDataMock);
        ServiceInterface serviceClass = SpyWrapper.newInstance(new ServiceClass(), ServiceInterface.class, reportCallback, SpyConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());

        assertThat(serviceClass.doSimpleService("hoi")).isEqualTo("This is fixed");
        assertThat(serviceClass.doSimpleService("not registered")).isNull();


    }


    @After
    public void tearDown() throws Exception {
        System.setProperty("no.steria.copito.doMock","false");
    }
}
