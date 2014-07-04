package no.steria.copito.recorder.logging;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class RecorderLogTest {

    private final static ByteArrayOutputStream standardOut = new ByteArrayOutputStream();
    private final static ByteArrayOutputStream otherLogger = new ByteArrayOutputStream();
    private static PrintStream originalStdOut;

    @BeforeClass
    public static void setUpClass() {
        originalStdOut = System.out;
        System.setOut(new PrintStream(standardOut));
    }

    @AfterClass
    public static void tearDownClass() {
        System.setOut(originalStdOut);

    }

    @Before
    public void setUp() {
        standardOut.reset();
    }

    @Test
    public void shouldWriteExpectedDebugMessage() {
        RecorderLog.debug("Watch out, I'm doing stuff!");
        RecorderLog.error("Houston we have a problem!");
        assertEquals(standardOut.toString(), "DEBUG: COPITO: Watch out, I'm doing stuff!\r\n" +
                "ERROR: COPITO: Houston we have a problem!\r\n");
    }

    @Test
    public void shouldWriteExpectedPrefix() {
        RecorderLog.DefaultRecorderLogger.setPrefix("Eplekake: ");
        RecorderLog.debug("Watch out, I'm doing stuff!");
        RecorderLog.error("Houston we have a problem!");
        assertEquals(standardOut.toString(), "DEBUG: Eplekake: Watch out, I'm doing stuff!\r\n" +
                "ERROR: Eplekake: Houston we have a problem!\r\n");
    }

    @Test
    public void shouldUseOtherLogger() {
        RecorderLog.setRecorderLogger(new DummyRecorderLogger());
        RecorderLog.debug("this");
        RecorderLog.error("that");
        assertEquals(otherLogger.toString(), "Whatch out! thisHouston we have a problem! that");
    }

    private class DummyRecorderLogger implements RecorderLogger {
        PrintStream printStream = new PrintStream(otherLogger);
        @Override
        public void error(String message) {
            printStream.print("Houston we have a problem! " + message);
        }

        @Override
        public void debug(String message) {
            printStream.print("Whatch out! " + message);
        }
    }
}