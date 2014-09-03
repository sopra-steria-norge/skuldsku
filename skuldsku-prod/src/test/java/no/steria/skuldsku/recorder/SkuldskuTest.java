package no.steria.skuldsku.recorder;

import no.steria.skuldsku.recorder.dbrecorder.DatabaseRecorder;
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

public class SkuldskuTest {

    @Mock
    private DatabaseRecorder databaseRecorder1;

    @Mock
    private DatabaseRecorder databaseRecorder2;

    @Mock
    private FilterConfig filterConfig;

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private HttpServletRequest request;
    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private HttpServletResponse response;
    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private FilterChain chain;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        SkuldskuAccessor.reset();
        Skuldsku.initialize(new SkuldskuConfig());
        Skuldsku.initializeDatabaseRecorders(Arrays.asList(databaseRecorder1, databaseRecorder2));
    }
    
    @Test(expected=IllegalStateException.class)
    public void canOnlyCallInitializeOnce() {
        // Note: initialize already called in setUp.
        Skuldsku.initialize(new SkuldskuConfig());
    }

    @Test
    public void shouldTurnOnWhenStartIsCalledAndOffWhenStopIsCalled() throws SQLException {
        assertFalse(Skuldsku.isRecordingOn());
        Skuldsku.start();
        assertTrue(Skuldsku.isRecordingOn());
        Skuldsku.stop();
        assertFalse(Skuldsku.isRecordingOn());
        Skuldsku.start();
        assertTrue(Skuldsku.isRecordingOn());
        Skuldsku.stop();
        assertFalse(Skuldsku.isRecordingOn());
    }

    @Test
    public void shouldAlsoTurnOnAndOffWhenNoDatabaseRecorder() throws SQLException {
        Skuldsku.initializeDatabaseRecorders(new ArrayList<DatabaseRecorder>(0));
        assertFalse(Skuldsku.isRecordingOn());
        Skuldsku.start();
        assertTrue(Skuldsku.isRecordingOn());
        Skuldsku.stop();
        Assert.assertFalse(Skuldsku.isRecordingOn());
    }

    @Test
    public void shouldNotTurnDbRecordingOffWhenAlreadyOff() {
        Mockito.reset(databaseRecorder1, databaseRecorder2); // ignore whatever happened in setUp()
        Skuldsku.stop();
        verifyNoMoreInteractions(databaseRecorder1);
        verifyNoMoreInteractions(databaseRecorder2);
    }

    @Test
    public void shouldTurnDbRecordingOnWhenRecordingTurnedOn() throws SQLException {
        Skuldsku.start();
        verify(databaseRecorder1, times(1)).start();
        verify(databaseRecorder2, times(1)).start();
    }

    @Test
    public void shouldNotTurnDbRecordingOnWhenAlreadyOn() throws SQLException {
        Skuldsku.start();
        Skuldsku.start();
        verify(databaseRecorder1, times(1)).start();
        verify(databaseRecorder2, times(1)).start();
    }

    @Test
    public void shouldTurnDbRecordingOffWhenRecordingTurnedOff() throws SQLException {
        Mockito.reset(databaseRecorder1, databaseRecorder2); // ignore whatever happened in setUp()
        Skuldsku.start();
        Skuldsku.stop();
        verify(databaseRecorder1, times(1)).stop(); // including the stop() that is executed in the setUp to "reset" the facade between tests.
        verify(databaseRecorder2, times(1)).stop(); // including the stop() that is executed in the setUp to "reset" the facade between tests.
    }
}
