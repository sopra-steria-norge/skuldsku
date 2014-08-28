package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

import org.junit.After;
import org.junit.Before;

public class MockTest {
    private final DummyReportCallback reportCallback = new DummyReportCallback();


    @Before
    public void setUp() throws Exception {
        System.setProperty("no.steria.skuldsku.doMock","true");
    }




    @After
    public void tearDown() throws Exception {
        System.setProperty("no.steria.skuldsku.doMock","false");
    }
}
