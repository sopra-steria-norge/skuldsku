package no.steria.skuldsku.recorder;

import no.steria.skuldsku.recorder.logging.RecorderLog;
import no.steria.skuldsku.recorder.logging.RecorderLogger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class SkuldskuNotInitializedTest {

    @Mock
    RecorderLogger recorderLogger;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        RecorderLog.setRecorderLogger(recorderLogger);
        SkuldskuAccessor.reset();
    }
    
    @Test
    public void startLogsErrorWhenNotInitialized() {
        Skuldsku.start();
        verify(recorderLogger, atLeastOnce()).error(anyString(), any(Throwable.class));
    }
    
    @Test
    public void stopLogsErrorInitialized() {
        Skuldsku.stop();
        verify(recorderLogger, atLeastOnce()).error(anyString(), any(Throwable.class));
    }
    
    @Test
    public void successWhenInitialized() {
        Skuldsku.initialize(new SkuldskuConfig());
        Skuldsku.start();
    }
}
