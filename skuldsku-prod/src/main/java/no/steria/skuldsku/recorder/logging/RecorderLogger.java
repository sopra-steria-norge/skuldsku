package no.steria.skuldsku.recorder.logging;

public interface RecorderLogger {

    void debug(String message);

    void error(String message);

    void info(String message);

    void error(String message, Throwable throwable);

    void warn(String message);

}
