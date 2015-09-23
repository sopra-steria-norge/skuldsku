package no.steria.skuldsku.recorder.java.recorder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JavaCallRecorderConfig {
    private AsyncMode asyncMode;
    private RecorderDebugLogger recorderDebugLogger;

    private static class EmptyDebugger implements RecorderDebugLogger {

        @Override
        public void debug(String message) {
        }
    }

    public static class Factory {
        private JavaCallRecorderConfig javaCallRecorderConfig = new JavaCallRecorderConfig();

        private Factory() {
        }

        public Factory withAsyncMode(AsyncMode mode) {
            javaCallRecorderConfig.asyncMode = mode;
            return this;
        }

        public Factory withDebugLogger(RecorderDebugLogger useLogger) {
            javaCallRecorderConfig.recorderDebugLogger = useLogger;
            return this;
        }

        public JavaCallRecorderConfig create() {
            return javaCallRecorderConfig;
        }
    }

    private JavaCallRecorderConfig() {
    }

    public static Factory factory() {
        return new Factory();
    }

    public AsyncMode getAsyncMode() {
        return asyncMode;
    }

    public RecorderDebugLogger debugLogger() {
        if (recorderDebugLogger == null) {
            recorderDebugLogger = new EmptyDebugger();
        }
        return recorderDebugLogger;
    }
}
