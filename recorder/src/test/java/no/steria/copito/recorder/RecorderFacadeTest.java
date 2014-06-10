package no.steria.copito.recorder;

import no.steria.copito.recorder.dbrecorder.DatabaseRecorder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class RecorderFacadeTest {

    @Mock
    private DatabaseRecorder databaseRecorder1;

    @Mock
    private DatabaseRecorder databaseRecorder2;

    private RecorderFacade recorderFacade;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        recorderFacade = new RecorderFacade(Arrays.asList(databaseRecorder1, databaseRecorder2));
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
        RecorderFacade recorderFacadeNoRecorder = new RecorderFacade(new ArrayList<DatabaseRecorder>(0  ));
        assertFalse(RecorderFacade.recordingIsOn());
        recorderFacadeNoRecorder.start();
        assertTrue(RecorderFacade.recordingIsOn());
        recorderFacadeNoRecorder.stop();
        Assert.assertFalse(RecorderFacade.recordingIsOn());
    }

    @Test
    public void shouldNotTurnDbRecordingOffWhenAlreadyOff() {
        verify(databaseRecorder1, atMost(1)).stop(); // could be called from setUp()
        verify(databaseRecorder2, atMost(1)).stop(); // could be called from setUp()
        recorderFacade.stop();
        verifyNoMoreInteractions(databaseRecorder1);
        verifyNoMoreInteractions(databaseRecorder2);
    }

    @Test
    public void shouldTurnDbRecordingOnWhenRecordingTurnedOn() throws SQLException {
        recorderFacade.start();
        verify(databaseRecorder1, times(1)).start();
        verify(databaseRecorder2, times(1)).start();
    }

    @Test
    public void shouldNotTurnDbRecordingOnWhenAlreadyOn() throws SQLException {
        recorderFacade.start();
        recorderFacade.start();
        verify(databaseRecorder1, times(1)).start();
        verify(databaseRecorder2, times(1)).start();
    }

    @Test
    public void shouldTurnDbRecordingOffWhenRecordingTurnedOff() throws SQLException {
        recorderFacade.start();
        recorderFacade.stop();
        verify(databaseRecorder1, times(2)).stop(); // including the stop() that is executed in the setUp to "reset" the facade between tests.
        verify(databaseRecorder2, times(2)).stop(); // including the stop() that is executed in the setUp to "reset" the facade between tests.
    }
}
