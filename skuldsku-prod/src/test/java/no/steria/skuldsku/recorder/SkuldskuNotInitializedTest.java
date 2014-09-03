package no.steria.skuldsku.recorder;

import org.junit.Before;
import org.junit.Test;

public class SkuldskuNotInitializedTest {

    @Before
    public void setUp() {
        SkuldskuAccessor.reset();
    }
    
    @Test(expected=IllegalStateException.class)
    public void startFailWhenNotInitialized() {
        Skuldsku.start();
    }
    
    @Test(expected=IllegalStateException.class)
    public void stopFailWhenNotInitialized() {
        Skuldsku.stop();
    }
    
    @Test
    public void successWhenInitialized() {
        Skuldsku.initialize(new SkuldskuConfig());
        Skuldsku.start();
    }
}
