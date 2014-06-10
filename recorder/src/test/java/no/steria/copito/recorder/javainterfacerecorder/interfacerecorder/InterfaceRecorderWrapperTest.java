package no.steria.copito.recorder.javainterfacerecorder.interfacerecorder;

import no.steria.copito.recorder.RecorderFacade;
import no.steria.copito.recorder.dbrecorder.DatabaseRecorder;
import no.steria.copito.recorder.javainterfacerecorder.serializer.ClassSerializer;
import no.steria.copito.recorder.javainterfacerecorder.serializer.ClassWithSimpleFields;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class InterfaceRecorderWrapperTest {

    private final DummyReportCallback reportCallback = new DummyReportCallback();

    RecorderFacade recorderFacade;

    @Before
    public void setUp() throws SQLException {
        recorderFacade = new RecorderFacade(new ArrayList<DatabaseRecorder>(0));
        recorderFacade.start();
    }

    @Test
    public void shouldHandleBasic() throws Exception {
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, reportCallback, InterfaceRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        String result = serviceClass.doSimpleService("MyName");

        assertThat(result).isEqualTo("Hello MyName");

        assertThat(reportCallback.getClassName()).isEqualTo("no.steria.copito.recorder.javainterfacerecorder.interfacerecorder.ServiceClass");
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

        assertThat(reportCallback.getClassName()).isEqualTo("no.steria.copito.recorder.javainterfacerecorder.interfacerecorder.ServiceClass");
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

        assertThat(reportCallback.getClassName()).isEqualTo("no.steria.copito.recorder.javainterfacerecorder.interfacerecorder.ServiceClass");
        assertThat(reportCallback.getMethodname()).isEqualTo("doWithPara");
        assertThat(reportCallback.getParameters()).isEqualTo("<null>");
        assertThat(reportCallback.getResult()).isEqualTo("<java.lang.String;This is it>");

    }

    @Test
    public void shouldHandleListsAsResults() throws Exception {
        ServiceInterface serviceClass = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class, reportCallback, InterfaceRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        List<String> result = serviceClass.returnList(new ClassWithSimpleFields().setIntval(42));

        assertThat(result).containsOnly("This", "is", "not", "null");

        assertThat(reportCallback.getClassName()).isEqualTo("no.steria.copito.recorder.javainterfacerecorder.interfacerecorder.ServiceClass");
        assertThat(reportCallback.getMethodname()).isEqualTo("returnList");

        ClassSerializer classSerializer = new ClassSerializer();

        ClassWithSimpleFields simpleFields = (ClassWithSimpleFields) classSerializer.asObject(reportCallback.getParameters());

        assertThat(simpleFields).isNotNull();
        assertThat(simpleFields.getIntval()).isEqualTo(42);

        @SuppressWarnings("unchecked") List<String> recodredResult = (List<String>) classSerializer.asObject(reportCallback.getResult());
        assertThat(recodredResult).containsOnly("This", "is", "not", "null");
    }


}
