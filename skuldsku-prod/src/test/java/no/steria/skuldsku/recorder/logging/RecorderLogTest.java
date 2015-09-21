package no.steria.skuldsku.recorder.logging;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;

import no.steria.skuldsku.recorder.logging.RecorderLog.DefaultRecorderLogger;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RecorderLogTest {

    private final static ByteArrayOutputStream standardOutAndErr = new ByteArrayOutputStream();
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
        PrintStream out = new PrintStream(standardOutAndErr);
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
        standardOutAndErr.reset();
    }

    @Test
    public void shouldWriteExpectedDebugMessage() {
        RecorderLog.debug("Watch out, I'm doing stuff!");
        RecorderLog.error("Houston we have a problem!");
        
        assertEquals("DEBUG: SKULDSKU: Watch out, I'm doing stuff!" + lineSeparator +
                "ERROR: SKULDSKU: Houston we have a problem!" + lineSeparator, standardOutAndErr.toString());
    }

    @Test
    public void shouldWriteExpectedPrefix() {
        RecorderLog.DefaultRecorderLogger.setPrefix("Eplekake: ");
        RecorderLog.debug("Watch out, I'm doing stuff!");
        RecorderLog.info("This just for information!");
        RecorderLog.error("Houston we have a problem!");
        assertEquals("DEBUG: Eplekake: Watch out, I'm doing stuff!" + lineSeparator +
                "INFO: Eplekake: This just for information!" + lineSeparator +
                "ERROR: Eplekake: Houston we have a problem!" + lineSeparator, standardOutAndErr.toString());
        RecorderLog.DefaultRecorderLogger.setPrefix("SKULDSKU: ");
    }

    @Test
    public void shouldLogStackTrace() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        sqlException.printStackTrace(pw);
        pw.flush();
        RecorderLog.error("Houston!", sqlException);
        assertEquals("ERROR: SKULDSKU: Houston!" + lineSeparator + baos.toString(), standardOutAndErr.toString());
    }

    @Test
    public void shouldUseOtherLogger() {
        RecorderLog.setRecorderLogger(new DummyRecorderLogger());
        RecorderLog.debug("this");
        RecorderLog.error("that");
        RecorderLog.error("such", new RuntimeException("deliberate!"));
        assertEquals(otherLogger.toString(), "Watch out! thisHouston we have a problem! thatthis is a stack trace!");
        RecorderLog.setRecorderLogger(new DefaultRecorderLogger());
    }

    private class DummyRecorderLogger implements RecorderLogger {
        PrintStream printStream = new PrintStream(otherLogger);
        @Override
        public void error(String message) {
            printStream.print("Houston we have a problem! " + message);
        }

        @Override
        public void info(String message) {
            printStream.print(message);
        }

        @Override
        public void error(String message, Throwable throwable) {
            printStream.print("this is a stack trace!");
        }

        @Override
        public void warn(String message) {
            printStream.print("good golly!");
        }

        @Override
        public void debug(String message) {
            printStream.print("Watch out! " + message);
        }
    }
}