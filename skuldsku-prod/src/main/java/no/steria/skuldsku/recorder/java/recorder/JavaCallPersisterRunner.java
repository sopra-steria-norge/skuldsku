package no.steria.skuldsku.recorder.java.recorder;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.recorder.logging.RecorderLog;

public class JavaCallPersisterRunner implements Runnable {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final JavaCallPersister javaCallPersister;
    private final String clientIdentifier;
    private final String className;
    private final String methodName;
    private final Object[] args;
    private final Object result;
    private final Throwable thrown;
    private final JavaCallRecorderConfig javaCallRecorderConfig;

    private JavaCallPersisterRunner(JavaCallPersister javaCallPersister, String clientIdentifier, String className, String methodName, Object[] args, Object result, Throwable thrown, JavaCallRecorderConfig javaCallRecorderConfig) {
        this.clientIdentifier = clientIdentifier;
        this.javaCallPersister = javaCallPersister;
        this.className = className;
        this.methodName = methodName;
        this.args = args;
        this.result = result;
        this.thrown = thrown;
        this.javaCallRecorderConfig = javaCallRecorderConfig;
    }

    private boolean doLog() {
        return Skuldsku.isRecordingOn();
    }

    private void logEvent() {
        RecorderLog.debug("LogRunner: log event " + className + "," + methodName);

        try {
            RecorderLog.debug("LogRunner: Calling report callback");
            javaCallPersister.event(new JavaCall(clientIdentifier, className, methodName, args, result, thrown));
        } catch (Throwable e) {
            RecorderLog.debug("LogRunner: exception logging " + e);
        }
    }

    @Override
    public void run() {
        if (javaCallRecorderConfig.getAsyncMode() == AsyncMode.ALL_ASYNC && !doLog()) {
            return;
        }
        logEvent();
    }

    public static void store(JavaCallPersister javaCallPersister, String clientIdentifier, String className, String methodName, Object[] args, Object result, Throwable thrown, JavaCallRecorderConfig javaCallRecorderConfig) {
        JavaCallPersisterRunner javaCallPersisterRunner = new JavaCallPersisterRunner(javaCallPersister, clientIdentifier, className, methodName, args, result, thrown, javaCallRecorderConfig);
        AsyncMode asyncMode = javaCallRecorderConfig.getAsyncMode();
        if (asyncMode == AsyncMode.ALL_ASYNC) {
            executorService.submit(javaCallPersisterRunner);
            return;
        }
        if (javaCallPersisterRunner.doLog()) {
            if (asyncMode == AsyncMode.ALL_SYNC) {
                javaCallPersisterRunner.logEvent();
            } else {
                executorService.submit(javaCallPersisterRunner);
            }
        }
    }

}
