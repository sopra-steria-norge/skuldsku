package no.steria.copito.recorder;

import no.steria.copito.recorder.dbrecorder.DatabaseRecorder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class RecorderFacadeTest {

    @Mock
    private DatabaseRecorder databaseRecorder;

    private RecorderFacade recorderFacade;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        recorderFacade = new RecorderFacade(databaseRecorder);
        recorderFacade.stop();
    }

    @Test
    public void shouldTurnOnWhenStartIsCalledAndOffWhenStopIsCalled() throws SQLException {
        recorderFacade.start();
        assertTrue(RecorderFacade.recordingIsOn());
        recorderFacade.stop();
        assertFalse(RecorderFacade.recordingIsOn());
        recorderFacade.start();
        assertTrue(RecorderFacade.recordingIsOn());
        recorderFacade.stop();
        assertFalse(RecorderFacade.recordingIsOn());
    }

    @Test
    public void shouldAlsoTurnOnAndOffWhenNoDatabaseRecorder() throws SQLException {
        RecorderFacade recorderFacadeNoRecorder = new RecorderFacade(null);
        assertFalse(RecorderFacade.recordingIsOn());
        recorderFacadeNoRecorder.start();
        assertTrue(RecorderFacade.recordingIsOn());
        recorderFacadeNoRecorder.stop();
        Assert.assertFalse(RecorderFacade.recordingIsOn());
    }

    @Test
    public void shouldNotTurnDbRecordingOffWhenAlreadyOff() {
        verify(databaseRecorder, atMost(1)).stop(); // could be called from setUp()
        recorderFacade.stop();
        verifyNoMoreInteractions(databaseRecorder);
    }

    @Test
    public void shouldTurnDbRecordingOnWhenRecordingTurnedOn() throws SQLException {
        recorderFacade.start();
        verify(databaseRecorder, times(1)).start();
    }

    @Test
    public void shouldNotTurnDbRecordingOnWhenAlreadyOn() throws SQLException {
        recorderFacade.start();
        recorderFacade.start();
        verify(databaseRecorder, times(1)).start();
    }

    @Test
    public void shouldTurnDbRecordingOffWhenRecordingTurnedOff() throws SQLException {
        recorderFacade.start();
        recorderFacade.stop();
        verify(databaseRecorder, times(2)).stop(); // including the stop() that is executed in the setUp to "reset" the facade between tests.
    }
}
