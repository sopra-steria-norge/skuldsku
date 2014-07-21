package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

import no.steria.skuldsku.recorder.Recorder;
import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;
import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassWithSimpleFields;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class InterfaceRecorderWrapperTest {

    private final DummyReportCallback reportCallback = new DummyReportCallback();

    @Before
    public void setUp() throws SQLException {
        Recorder.start();
    }

    @Test
    public void shouldHandleBasic() throws Exception {
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, reportCallback, InterfaceRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        String result = serviceClass.doSimpleService("MyName");

        assertThat(result).isEqualTo("Hello MyName");

        assertThat(reportCallback.getClassName()).isEqualTo("no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.ServiceClass");
        assertThat(reportCallback.getMethodname()).isEqualTo("doSimpleService");
        assertThat(reportCallback.getParameters()).isEqualTo("<java.lang.String;MyName>");
        assertThat(reportCallback.getResult()).isEqualTo("<java.lang.String;Hello MyName>");
    }

    @Test
    public void shouldIgnoreFields() throws Exception {
        Method doWithPara = ServiceClass.class.getMethod("doWithPara", ServiceParameterClass.class);
        Class<?> ignore = ServiceParameterClass.class;
        InterfaceRecorderConfig interfaceRecorderConfig = InterfaceRecorderConfig.factory()
                .withAsyncMode(AsyncMode.ALL_SYNC)
                .ignore(ServiceClass.class, doWithPara, ignore)
                .create();
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, reportCallback, interfaceRecorderConfig);

        ServiceParameterClass para = new ServiceParameterClass();
        para.setInfo("This is it");
        String result = serviceClass.doWithPara(para);

        assertThat(result).isEqualTo("This is it");

        assertThat(reportCallback.getClassName()).isEqualTo("no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.ServiceClass");
        assertThat(reportCallback.getMethodname()).isEqualTo("doWithPara");
        assertThat(reportCallback.getParameters()).isEqualTo("<null>");
        assertThat(reportCallback.getResult()).isEqualTo("<java.lang.String;This is it>");

    }

    @Test
    public void shouldIgnoreParametersCall() throws Exception {
        Class<?> ignore = ServiceParameterClass.class;
        InterfaceRecorderConfig interfaceRecorderConfig = InterfaceRecorderConfig.factory()
                .withAsyncMode(AsyncMode.ALL_SYNC)
                .ignore(ServiceClass.class, null, ignore)
                .create();
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, reportCallback, interfaceRecorderConfig);

        ServiceParameterClass para = new ServiceParameterClass();
        para.setInfo("This is it");
        String result = serviceClass.doWithPara(para);

        assertThat(result).isEqualTo("This is it");

        assertThat(reportCallback.getClassName()).isEqualTo("no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.ServiceClass");
        assertThat(reportCallback.getMethodname()).isEqualTo("doWithPara");
        assertThat(reportCallback.getParameters()).isEqualTo("<null>");
        assertThat(reportCallback.getResult()).isEqualTo("<java.lang.String;This is it>");

    }

    @Test
    public void shouldHandleListsAsResults() throws Exception {
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, reportCallback, InterfaceRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        List<String> result = serviceClass.returnList(new ClassWithSimpleFields().setIntval(42));

        assertThat(result).containsOnly("This", "is", "not", "null");

        assertThat(reportCallback.getClassName()).isEqualTo("no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.ServiceClass");
        assertThat(reportCallback.getMethodname()).isEqualTo("returnList");

        ClassSerializer classSerializer = new ClassSerializer();

        ClassWithSimpleFields simpleFields = (ClassWithSimpleFields) classSerializer.asObject(reportCallback.getParameters());

        assertThat(simpleFields).isNotNull();
        assertThat(simpleFields.getIntval()).isEqualTo(42);

        @SuppressWarnings("unchecked") List<String> recordedResult = (List<String>) classSerializer.asObject(reportCallback.getResult());
        assertThat(recordedResult).containsOnly("This", "is", "not", "null");
    }

    @Test
    public void shouldInvokeMethodButNotRecordWhenRecordingIsOff() throws SQLException {
        ServiceClass serviceClassObject = mock(ServiceClass.class);
        when(serviceClassObject.doSimpleService(anyString())).thenReturn("Hello MyName");
        Recorder.stop();
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(serviceClassObject, ServiceInterface.class, reportCallback, InterfaceRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        String result = serviceClass.doSimpleService("MyName");

        assertThat(result).isEqualTo("Hello MyName");

        assertNull(reportCallback.getClassName());
        assertNull(reportCallback.getMethodname());
        assertNull(reportCallback.getParameters());
        assertNull(reportCallback.getResult());
        Recorder.start();
    }

    @Test
    public void shouldHandleFile() throws Exception {
        InterfaceRecorderConfig interfaceRecorderConfig = InterfaceRecorderConfig.factory()
                .withAsyncMode(AsyncMode.ALL_SYNC)
                .ignore(ServiceClass.class, null, File.class)
                .create();
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, reportCallback, interfaceRecorderConfig);
        File tempFile = File.createTempFile("test", "txt");
        PrintWriter printWriter = new PrintWriter(new FileWriter(tempFile));
        printWriter.append("This is Johnny");
        printWriter.close();

        String s = serviceClass.readAFile("Hello, ",tempFile);
        assertThat(s).isEqualTo("Hello, This is Johnny");
        assertTrue("could not delete resource file", tempFile.delete());

        ClassSerializer classSerializer = new ClassSerializer();
        System.out.println(reportCallback.getParameters());
        assertThat(reportCallback.getMethodname()).isEqualTo("readAFile");
        assertThat(classSerializer.asObject(reportCallback.getResult())).isEqualTo("Hello, This is Johnny");

        assertThat(reportCallback.getParameters()).isEqualTo("<java.lang.String;Hello, >;<null>");
    }

    @Test
    public void shouldIgnoreTypesOfClasses() throws Exception {
        InterfaceRecorderConfig interfaceRecorderConfig = InterfaceRecorderConfig.factory()
                .withAsyncMode(AsyncMode.ALL_SYNC)
                .ignore(null, null, File.class)
                .create();
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, reportCallback, interfaceRecorderConfig);
        File tempFile = File.createTempFile("test", "txt");
        PrintWriter printWriter = new PrintWriter(new FileWriter(tempFile));
        printWriter.append("This is Johnny");
        printWriter.close();

        String s = serviceClass.readAFile("Hello, ",tempFile);
        assertThat(s).isEqualTo("Hello, This is Johnny");
        assertTrue("could not delete resource file", tempFile.delete());

        ClassSerializer classSerializer = new ClassSerializer();
        System.out.println(reportCallback.getParameters());
        assertThat(reportCallback.getMethodname()).isEqualTo("readAFile");
        assertThat(classSerializer.asObject(reportCallback.getResult())).isEqualTo("Hello, This is Johnny");

        assertThat(reportCallback.getParameters()).isEqualTo("<java.lang.String;Hello, >;<null>");

    }
}
