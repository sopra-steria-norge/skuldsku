package no.steria.skuldsku.recorder.java.recorder;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.SkuldskuAccessor;
import no.steria.skuldsku.recorder.SkuldskuConfig;
import no.steria.skuldsku.recorder.java.serializer.ClassSerializer;
import no.steria.skuldsku.recorder.java.serializer.ClassWithSimpleFields;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class InterfaceRecorderWrapperTest {

    private final DummyJavaIntefaceCallPersister dummyJavaIntefaceCallPersister = new DummyJavaIntefaceCallPersister();

    @Before
    public void setUp() throws SQLException {
        SkuldskuAccessor.reset();
        Skuldsku.initialize(new SkuldskuConfig());
        Skuldsku.start();
    }

    @Test
    public void shouldHandleBasic() throws Exception {
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, dummyJavaIntefaceCallPersister, JavaCallRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        String result = serviceClass.doSimpleService("MyName");

        assertThat(result).isEqualTo("Hello MyName");

        assertThat(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getClassName()).isEqualTo("no.steria.skuldsku.recorder.java.recorder.ServiceClass");
        assertThat(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getMethodname()).isEqualTo("doSimpleService");
        assertThat(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getParameters()).isEqualTo("<array;[Ljava.lang.Object&semi;<java.lang.String;MyName>>");
        assertThat(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getResult()).isEqualTo("<java.lang.String;Hello MyName>");
    }
    
    @Test
    public void shouldHandleExceptions() throws Exception {
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, dummyJavaIntefaceCallPersister, JavaCallRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        String result = null;
        try {
            result = serviceClass.doSimpleService("exception");
            fail("expected exception before this line");
        } catch (RuntimeException e) {
            // Ignore
        }

        assertThat(result).isEqualTo(null);

        assertThat(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getClassName()).isEqualTo("no.steria.skuldsku.recorder.java.recorder.ServiceClass");
        assertThat(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getMethodname()).isEqualTo("doSimpleService");
        assertThat(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getParameters()).isEqualTo("<array;[Ljava.lang.Object&semi;<java.lang.String;exception>>");
        assertThat(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getResult()).isEqualTo("<null>");
        assertThat(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getThrown()).isEqualTo("<java.lang.IllegalStateException;detailMessage=exception;cause=<duplicate;0>;stackTrace=<array;[Ljava.lang.StackTraceElement&semi>;suppressedExceptions=<list>>");
    }


    @Test
    public void shouldHandleListsAsResults() throws Exception {
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, dummyJavaIntefaceCallPersister, JavaCallRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        List<String> result = serviceClass.returnList(new ClassWithSimpleFields().setIntval(42));

        assertThat(result).containsOnly("This", "is", "not", "null");

        assertThat(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getClassName()).isEqualTo("no.steria.skuldsku.recorder.java.recorder.ServiceClass");
        assertThat(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getMethodname()).isEqualTo("returnList");

        ClassSerializer classSerializer = new ClassSerializer();

        final Object[] parameterArray = (Object[]) classSerializer.asObject(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getParameters());
        ClassWithSimpleFields simpleFields = (ClassWithSimpleFields) parameterArray[0];

        assertThat(simpleFields).isNotNull();
        assertThat(simpleFields.getIntval()).isEqualTo(42);

        @SuppressWarnings("unchecked") List<String> recordedResult = (List<String>) classSerializer.asObject(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getResult());
        assertThat(recordedResult).containsOnly("This", "is", "not", "null");
    }

    @Test
    public void shouldInvokeMethodButNotRecordWhenRecordingIsOff() throws SQLException {
        ServiceClass serviceClassObject = mock(ServiceClass.class);
        when(serviceClassObject.doSimpleService(anyString())).thenReturn("Hello MyName");
        Skuldsku.stop();
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(serviceClassObject, ServiceInterface.class, dummyJavaIntefaceCallPersister, JavaCallRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        String result = serviceClass.doSimpleService("MyName");

        assertThat(result).isEqualTo("Hello MyName");

        assertNull(dummyJavaIntefaceCallPersister.getJavaInterfaceCall());
        Skuldsku.start();
    }

    @Test
    @Ignore
    @Deprecated
    public void shouldHandleFile() throws Exception {
        JavaCallRecorderConfig javaCallRecorderConfig = JavaCallRecorderConfig.factory()
                .withAsyncMode(AsyncMode.ALL_SYNC)
                .create();
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, dummyJavaIntefaceCallPersister, javaCallRecorderConfig);
        File tempFile = File.createTempFile("test", "txt");
        PrintWriter printWriter = new PrintWriter(new FileWriter(tempFile));
        printWriter.append("This is Johnny");
        printWriter.close();

        String s = serviceClass.readAFile("Hello, ",tempFile);
        assertThat(s).isEqualTo("Hello, This is Johnny");
        assertTrue("could not delete resource file", tempFile.delete());

        ClassSerializer classSerializer = new ClassSerializer();
        System.out.println(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getParameters());
        assertThat(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getMethodname()).isEqualTo("readAFile");
        assertThat(classSerializer.asObject(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getResult())).isEqualTo("Hello, This is Johnny");

        assertThat(dummyJavaIntefaceCallPersister.getJavaInterfaceCall().getParameters()).isEqualTo("<java.lang.String;Hello, >;<null>");
    }
}
