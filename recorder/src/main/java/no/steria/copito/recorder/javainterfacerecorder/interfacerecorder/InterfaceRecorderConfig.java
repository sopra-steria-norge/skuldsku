package no.steria.copito.recorder.javainterfacerecorder.interfacerecorder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InterfaceRecorderConfig {
    private AsyncMode asyncMode;
    private List<IgnorePara> ignores = new ArrayList<>();
    private RecorderDebugLogger recorderDebugLogger;

    private static class EmptyDebugger implements RecorderDebugLogger {

        @Override
        public void debug(String message) {
        }
    }

    public boolean isIgnored(String className, String methodName, Object para) {
        if (para == null) {
            return false;
        }
        for (IgnorePara ignorePara : ignores) {
            if ((ignorePara.getServiceClass() == null || ignorePara.getServiceClass().getName().equals(className)) &&
                    (ignorePara.getServiceMethod() == null || ignorePara.getServiceMethod().getName().equals(methodName)) &&
                    para.getClass().isAssignableFrom(ignorePara.getIgnore())
                    ) {
                return true;
            }
        }
        return false;
    }

    public static class Factory {
        private InterfaceRecorderConfig interfaceRecorderConfig = new InterfaceRecorderConfig();

        private Factory() {
        }

        public Factory withAsyncMode(AsyncMode mode) {
            interfaceRecorderConfig.asyncMode = mode;
            return this;
        }

        public Factory withDebugLogger(RecorderDebugLogger useLogger) {
            interfaceRecorderConfig.recorderDebugLogger = useLogger;
            return this;
        }

        public InterfaceRecorderConfig create() {
            return interfaceRecorderConfig;
        }

        public Factory ignore(Class<?> serviceClass, Method serviceMethod, Class<?> ignore) {
            interfaceRecorderConfig.ignores.add(new IgnorePara(serviceClass,serviceMethod,ignore));
            return this;
        }
    }

    private InterfaceRecorderConfig() {
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
