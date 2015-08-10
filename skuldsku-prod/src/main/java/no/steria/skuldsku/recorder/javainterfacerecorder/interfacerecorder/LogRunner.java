package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;


import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;
import no.steria.skuldsku.recorder.logging.RecorderLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogRunner implements Runnable {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final JavaIntefaceCallPersister javaIntefaceCallPersister;
    private final String className;
    private final String methodName;
    private final Object[] args;
    private final Object result;
    private final InterfaceRecorderConfig interfaceRecorderConfig;

    private LogRunner(JavaIntefaceCallPersister javaIntefaceCallPersister, String className, String methodName, Object[] args, Object result, InterfaceRecorderConfig interfaceRecorderConfig) {
        this.javaIntefaceCallPersister = javaIntefaceCallPersister;
        this.className = className;
        this.methodName = methodName;
        this.args = args;
        this.result = result;
        this.interfaceRecorderConfig = interfaceRecorderConfig;
    }

    private boolean doLog() {
        return Skuldsku.isRecordingOn();
    }

    private void logEvent() {
        RecorderLog.debug("LogRunner: log event " + className + "," + methodName);

        try {
            ClassSerializer classSerializer = new ClassSerializer();
            StringBuilder parameters = new StringBuilder();
            if (args != null) {
                boolean first = true;
                for (Object para : args) {
                    String parStr = para != null ? para.getClass().getName() : null;
                    RecorderLog.debug("LogRunner: writing para " + parStr);
                    Object toLog = logObject(para);
                    if (!first) {
                        parameters.append(";");
                    }
                    first = false;
                    parameters.append(classSerializer.asString(toLog));
                }
            }
            RecorderLog.debug("LogRunner: writing result");
            String resultStr = classSerializer.asString(result);
            RecorderLog.debug("LogRunner: Calling report callback");

            javaIntefaceCallPersister.event(new JavaInterfaceCall(className, methodName, parameters.toString(), resultStr));
        } catch (Throwable e) {
            RecorderLog.debug("LogRunner: exeption logging " + e);
        }
    }

    private Object logObject(Object para) {
        if (interfaceRecorderConfig.isIgnored(className, methodName, para)) {
            return null;
        }
        return para;
    }


    public static void log(JavaIntefaceCallPersister javaIntefaceCallPersister, String className, String methodName, Object[] args, Object result, InterfaceRecorderConfig interfaceRecorderConfig) {
        LogRunner logRunner = new LogRunner(javaIntefaceCallPersister, className, methodName, args, result, interfaceRecorderConfig);
        AsyncMode asyncMode = interfaceRecorderConfig.getAsyncMode();
        if (asyncMode == AsyncMode.ALL_ASYNC) {
            executorService.submit(logRunner);
            return;
        }
        if (logRunner.doLog()) {
            if (asyncMode == AsyncMode.ALL_SYNC) {
                logRunner.logEvent();
            } else {
                executorService.submit(logRunner);
            }
        }
    }

    @Override
    public void run() {
        if (interfaceRecorderConfig.getAsyncMode() == AsyncMode.ALL_ASYNC && !doLog()) {
            return;
        }
        logEvent();
    }
}
