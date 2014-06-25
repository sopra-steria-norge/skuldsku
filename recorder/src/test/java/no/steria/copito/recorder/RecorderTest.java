package no.steria.copito.recorder;

import no.steria.copito.recorder.dbrecorder.DatabaseRecorder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertFalse;
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
}
