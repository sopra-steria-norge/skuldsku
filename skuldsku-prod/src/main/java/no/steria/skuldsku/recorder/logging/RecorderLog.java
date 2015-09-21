package no.steria.skuldsku.recorder.logging;

public class RecorderLog {

    private static RecorderLogger recorderLogger = new DefaultRecorderLogger();

    private static String prefix = "SKULDSKU: ";

    public static void debug(String message) {
        recorderLogger.debug(message);
    }

    public static void info(String message) {
        recorderLogger.info(message);
    }

    public static void warn(String message) {
        recorderLogger.warn(message);
    }

    public static void error(String message) {
        recorderLogger.error(message);
    }

    public static void error(String message, Throwable throwable){
        recorderLogger.error(message, throwable);
    }

    public static void setRecorderLogger(RecorderLogger newRecorderLogger) {
        recorderLogger = newRecorderLogger;
    }

    public static class DefaultRecorderLogger implements RecorderLogger {

        @Override
        public void debug(String message) {
            System.err.println("DEBUG: " + prefix + message);
            System.err.flush();
        }

        @Override
        public void error(String message) {
            System.err.println("ERROR: " + prefix + message);
            System.err.flush();
        }

        @Override
        public void info(String message) {
            System.err.println("INFO: " + prefix + message);
            System.err.flush();
        }

        @Override
        public void warn(String message) {
            System.err.println("WARN: " + prefix + message);
            System.err.flush();
        }

        @Override
        public void error(String message, Throwable throwable) {
            System.err.println("ERROR: " + prefix + message);
            throwable.printStackTrace();
            System.err.flush();
        }

        public static void setPrefix(String newPrefix) {
            prefix = newPrefix;
        }
    }
}
