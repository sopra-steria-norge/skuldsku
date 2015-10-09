package no.steria.skuldsku.recorder.java.mock;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.SkuldskuAccessor;
import no.steria.skuldsku.recorder.SkuldskuConfig;
import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.recorder.java.recorder.AsyncMode;
import no.steria.skuldsku.recorder.java.recorder.DummyJavaIntefaceCallPersister;
import no.steria.skuldsku.recorder.java.recorder.InterfaceRecorderWrapper;
import no.steria.skuldsku.recorder.java.recorder.JavaCallRecorderConfig;
import no.steria.skuldsku.recorder.java.recorder.ServiceClass;
import no.steria.skuldsku.recorder.java.recorder.ServiceInterface;

import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RecordedDataMockTest {

    private final DummyJavaIntefaceCallPersister dummyJavaIntefaceCallPersister = new DummyJavaIntefaceCallPersister();

    @Before
    public void setUp() throws SQLException {
        SkuldskuAccessor.reset();
        Skuldsku.initialize(new SkuldskuConfig());
        Skuldsku.start();
    }

    @Test
    public void shouldHandleBasic() throws Throwable {
        // Setup:
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, dummyJavaIntefaceCallPersister, JavaCallRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        String result = serviceClass.doSimpleService("MyName");
        
        // Test:
        final RecordedDataMock mock = new RecordedDataMock(Collections.list(dummyJavaIntefaceCallPersister.getJavaInterfaceCall()));
        final Object resultFromMock = mock.invoke(ServiceInterface.class, "", ServiceInterface.class.getMethod("doSimpleService", String.class), new Object[] {"MyName"});
        
        assertThat(resultFromMock).isEqualTo(result);
    }
    
    @Test
    public void shouldHandleException() throws Throwable {
        // Setup:
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, dummyJavaIntefaceCallPersister, JavaCallRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        final Exception thrown;
        try {
            serviceClass.doSimpleService("exception");
            fail("Expected exception.");
            return;
        } catch (IllegalStateException e) {
            thrown = e;
            e.printStackTrace();
        }
        
        // Test:
        final RecordedDataMock mock = new RecordedDataMock(Collections.list(dummyJavaIntefaceCallPersister.getJavaInterfaceCall()));
        try {
            mock.invoke(ServiceInterface.class, "", ServiceInterface.class.getMethod("doSimpleService", String.class), new Object[] {"exception"});
            fail("Expected exception.");
            return;
        } catch (IllegalStateException e) {
            assertThat(e.getClass()).isEqualTo(thrown.getClass());
            assertThat(e.getMessage()).isEqualTo(thrown.getMessage());
            assertThat(e.getCause()).isEqualTo(thrown.getCause());
        }
    }
    
    @Test
    public void shouldHandleMultipleMethodCalls() throws Throwable {
        // Setup:
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, dummyJavaIntefaceCallPersister, JavaCallRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        final List<JavaCall> calls = new ArrayList<>();
        serviceClass.doSimpleService("MyName1");
        calls.add(dummyJavaIntefaceCallPersister.getJavaInterfaceCall());
        String correctResult = serviceClass.doSimpleService("MyName2");
        calls.add(dummyJavaIntefaceCallPersister.getJavaInterfaceCall());
        serviceClass.doSimpleService("MyName3");
        calls.add(dummyJavaIntefaceCallPersister.getJavaInterfaceCall());
        
        // Test:
        final RecordedDataMock mock = new RecordedDataMock(calls);
        final Object resultFromMock = mock.invoke(ServiceInterface.class, "", ServiceInterface.class.getMethod("doSimpleService", String.class), new Object[] {"MyName2"});
        
        assertThat(resultFromMock).isEqualTo(correctResult);
    }
    
    
    public interface HandleReturningAnArgumentService {
        Object returnTheArgument(Object o);
    }
    static final class HandleReturningAnArgumentImpl implements HandleReturningAnArgumentService {
        public Object returnTheArgument(Object o) {
            return o;
        }
    }
    
    @Test
    @Ignore("Not implemented: Probably easiest to implement by serializing the entire JavaCall object in one go.")
    public void shouldHandleReturningAnArgument() throws Throwable {
        final HandleReturningAnArgumentService serviceClass = InterfaceRecorderWrapper.newInstance(new HandleReturningAnArgumentImpl(), HandleReturningAnArgumentService.class, dummyJavaIntefaceCallPersister, JavaCallRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        final Object parameter = new Object();
        final Object result = serviceClass.returnTheArgument(parameter);
        assertThat(result).isSameAs(parameter);
        
        final RecordedDataMock mock = new RecordedDataMock(Collections.list(dummyJavaIntefaceCallPersister.getJavaInterfaceCall()));
        final Object resultFromMock = mock.invoke(HandleReturningAnArgumentService.class, "",
                HandleReturningAnArgumentService.class.getMethod("returnTheArgument", Object.class),
                new Object[] {parameter});
        
        assertThat(resultFromMock).isSameAs(parameter);
    }
}
