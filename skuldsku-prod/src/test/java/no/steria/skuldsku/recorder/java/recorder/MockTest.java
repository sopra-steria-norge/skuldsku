package no.steria.skuldsku.recorder.java.recorder;

import org.junit.After;
import org.junit.Before;

public class MockTest {
    private final DummyJavaIntefaceCallPersister reportCallback = new DummyJavaIntefaceCallPersister();


    @Before
    public void setUp() throws Exception {
        System.setProperty("no.steria.skuldsku.doMock","true");
    }




    @After
    public void tearDown() throws Exception {
        System.setProperty("no.steria.skuldsku.doMock","false");
    }
}
