package no.steria.copito.recorder.logging;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class RecorderLogTest {

    private final static ByteArrayOutputStream standardOut = new ByteArrayOutputStream();
    private final static ByteArrayOutputStream otherLogger = new ByteArrayOutputStream();
    private static PrintStream originalStdOut;
    private static PrintStream originalStdErr;
    private final SQLException sqlException = new SQLException("deliberate");
    private static String lineSeparator;

    @BeforeClass
    public static void setUpClass() {
        lineSeparator = System.lineSeparator();
        originalStdOut = System.out;
        originalStdErr = System.err;
        PrintStream out = new PrintStream(standardOut);
        System.setOut(out);
        System.setErr(out);
    }

    @AfterClass
    public static void tearDownClass() {
        System.setOut(originalStdOut);
        System.setErr(originalStdErr);

    }

    @Before
    public void setUp() {
        standardOut.reset();
    }

    @Test
    public void shouldWriteExpectedDebugMessage() {
        RecorderLog.debug("Watch out, I'm doing stuff!");
        RecorderLog.error("Houston we have a problem!");
        assertEquals("DEBUG: COPITO: Watch out, I'm doing stuff!" + lineSeparator +
                "ERROR: COPITO: Houston we have a problem!" + lineSeparator, standardOut.toString());
    }

    @Test
    public void shouldWriteExpectedPrefix() {
        RecorderLog.DefaultRecorderLogger.setPrefix("Eplekake: ");
        RecorderLog.debug("Watch out, I'm doing stuff!");
        RecorderLog.error("Houston we have a problem!");
        assertEquals("DEBUG: Eplekake: Watch out, I'm doing stuff!" + lineSeparator +
                "ERROR: Eplekake: Houston we have a problem!" + lineSeparator, standardOut.toString());
        RecorderLog.DefaultRecorderLogger.setPrefix("COPITO: ");
    }

    @Test
    public void shouldLogStackTrace() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        sqlException.printStackTrace(pw);
        pw.flush();
        RecorderLog.error("Houston!", sqlException);
        assertEquals("ERROR: COPITO: Houston!" + lineSeparator + baos.toString(), standardOut.toString());
    }

    @Test
    public void shouldUseOtherLogger() {
        RecorderLog.setRecorderLogger(new DummyRecorderLogger());
        RecorderLog.debug("this");
        RecorderLog.error("that");
        RecorderLog.error("such", new RuntimeException("deliberate!"));
        assertEquals(otherLogger.toString(), "Watch out! thisHouston we have a problem! thatthis is a stack trace!");
    }

    private class DummyRecorderLogger implements RecorderLogger {
        PrintStream printStream = new PrintStream(otherLogger);
        @Override
        public void error(String message) {
            printStream.print("Houston we have a problem! " + message);
        }

        @Override
        public void error(String message, Throwable throwable) {
            printStream.print("this is a stack trace!");
        }

        @Override
        public void debug(String message) {
            printStream.print("Watch out! " + message);
        }
    }
}