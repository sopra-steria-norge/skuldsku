package no.steria.copito.recorder.logging;

public interface RecorderLogger {

    void debug(String message);

    void error(String message);

    void error(String message, Throwable throwable);

}
