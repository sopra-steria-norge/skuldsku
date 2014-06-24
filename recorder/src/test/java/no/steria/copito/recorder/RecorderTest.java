package no.steria.copito.recorder;

import no.steria.copito.recorder.dbrecorder.DatabaseRecorder;
import no.steria.copito.recorder.httprecorder.CallReporter;
import no.steria.copito.recorder.httprecorder.testjetty.InMemoryReporter;
import no.steria.copito.recorder.httprecorder.testjetty.TestFilter;
import no.steria.copito.recorder.javainterfacerecorder.interfacerecorder.*;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class RecorderTest {

    @Mock
    private DatabaseRecorder databaseRecorder1;

    @Mock
    private DatabaseRecorder databaseRecorder2;

    @Mock
    private FilterConfig filterConfig;

    private Recorder recorder;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private HttpServletRequest request;
    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private HttpServletResponse response;
    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private FilterChain chain;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        recorder = new Recorder(Arrays.asList(databaseRecorder1, databaseRecorder2));
        recorder.resetFilterRegister();
        recorder.resetReportCallbackRegister();
        recorder.stop();
    }

    @Test
    public void shouldTurnOnWhenStartIsCalledAndOffWhenStopIsCalled() throws SQLException {
        recorder.start();
        assertTrue(Recorder.recordingIsOn());
        recorder.stop();
        assertFalse(Recorder.recordingIsOn());
        recorder.start();
        assertTrue(Recorder.recordingIsOn());
        recorder.stop();
        assertFalse(Recorder.recordingIsOn());
    }

    @Test
    public void shouldAlsoTurnOnAndOffWhenNoDatabaseRecorder() throws SQLException {
        Recorder recorderFacadeNoRecorder = new Recorder(new ArrayList<DatabaseRecorder>(0));
        assertFalse(Recorder.recordingIsOn());
        recorderFacadeNoRecorder.start();
        assertTrue(Recorder.recordingIsOn());
        recorderFacadeNoRecorder.stop();
        Assert.assertFalse(Recorder.recordingIsOn());
    }

    @Test
    public void shouldNotTurnDbRecordingOffWhenAlreadyOff() {
        Mockito.reset(databaseRecorder1, databaseRecorder2); // ignore whatever happened in setUp()
        recorder.stop();
        verifyNoMoreInteractions(databaseRecorder1);
        verifyNoMoreInteractions(databaseRecorder2);
    }

    @Test
    public void shouldTurnDbRecordingOnWhenRecordingTurnedOn() throws SQLException {
        recorder.start();
        verify(databaseRecorder1, times(1)).start();
        verify(databaseRecorder2, times(1)).start();
    }

    @Test
    public void shouldNotTurnDbRecordingOnWhenAlreadyOn() throws SQLException {
        recorder.start();
        recorder.start();
        verify(databaseRecorder1, times(1)).start();
        verify(databaseRecorder2, times(1)).start();
    }

    @Test
    public void shouldTurnDbRecordingOffWhenRecordingTurnedOff() throws SQLException {
        Mockito.reset(databaseRecorder1, databaseRecorder2); // ignore whatever happened in setUp()
        recorder.start();
        recorder.stop();
        verify(databaseRecorder1, times(1)).stop(); // including the stop() that is executed in the setUp to "reset" the facade between tests.
        verify(databaseRecorder2, times(1)).stop(); // including the stop() that is executed in the setUp to "reset" the facade between tests.
    }

    @Test
    public void shouldExportDbInteractions() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        recorder.exportTo(outputStream);
        verify(databaseRecorder1, times(1)).exportTo(any(PrintWriter.class));
        verify(databaseRecorder2, times(1)).exportTo(any(PrintWriter.class));
    }

    @Test
    public void shouldExportHttpInteractions() throws ServletException, IOException, SQLException {
        @SuppressWarnings("unchecked")
        Enumeration<String> headerNames = new IteratorEnumeration(new ArrayList<String>().iterator());

        when(request.getHeaderNames()).thenReturn(headerNames);
        recorder.start();
        CallReporter callReporter = new InMemoryReporter();
        TestFilter testFilter = new TestFilter(callReporter);
        testFilter.init(filterConfig);
        testFilter.doFilter(request, response, chain);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        recorder.exportTo(outputStream);
        String content = outputStream.toString();
        recorder.resetFilterRegister();
        assertEquals("<no.steria.copito.recorder.httprecorder.ReportObject;readInputStream=<null>" +
                ";parameters=<map>;method=;path=;output=<null>;headers=<map>>", content);
    }

    @Test
    public void shouldExportJavaInterfaceInteractions() throws IOException, SQLException {
        recorder.start();
        prepareDataMock();
        ServiceInterface serviceClass1 = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class,
                new DummyReportCallback(), InterfaceRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());
        ServiceInterface serviceClass2 = InterfaceRecorderWrapper.newInstance(new ServiceClass(), ServiceInterface.class,
                new DummyReportCallback(), InterfaceRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create());

        serviceClass1.doSimpleService("ServiceClass1");
        serviceClass2.doSimpleService("ServiceClass2");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        recorder.exportTo(outputStream);

        String content = outputStream.toString();
        assertEquals("no.steria.copito.recorder.javainterfacerecorder.interfacerecorder.ServiceClass;doSimpleService;" +
                "<java.lang.String;ServiceClass1>;<java.lang.String;Hello ServiceClass1>no.steria.copito.recorder." +
                "javainterfacerecorder.interfacerecorder.ServiceClass;doSimpleService;<java.lang.String;ServiceClass2>;" +
                "<java.lang.String;Hello ServiceClass2>", content);
        recorder.resetReportCallbackRegister();
    }

    private void prepareDataMock() {
        List<RecordObject> recorded = new ArrayList<>();
        RecordedDataMock recordedDataMock = new RecordedDataMock(recorded);
        MockRegistration.registerMock(ServiceInterface.class, recordedDataMock);
    }
}
