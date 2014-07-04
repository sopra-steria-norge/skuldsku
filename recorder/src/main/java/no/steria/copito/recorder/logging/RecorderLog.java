package no.steria.copito.recorder.logging;

public class RecorderLog {

    private static RecorderLogger recorderLogger = new DefaultRecorderLogger();

    private static String prefix = "COPITO: ";

    public static void debug(String message) {
        recorderLogger.debug(message);
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
            System.out.println("DEBUG: " + prefix + message);
        }

        @Override
        public void error(String message) {
            System.out.println("ERROR: " + prefix + message);
        }

        @Override
        public void error(String message, Throwable throwable) {
            System.out.println("ERROR: " + prefix + message);
            throwable.printStackTrace();
        }

        public static void setPrefix(String newPrefix) {
            prefix = newPrefix;
        }
    }
}
