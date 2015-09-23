package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;


import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;
import no.steria.skuldsku.recorder.logging.RecorderLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MethodCallStorageRunner implements Runnable {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final JavaIntefaceCallPersister javaIntefaceCallPersister;
    private final String clientIdentifier;
    private final String className;
    private final String methodName;
    private final Object[] args;
    private final Object result;
    private final InterfaceRecorderConfig interfaceRecorderConfig;

    private MethodCallStorageRunner(JavaIntefaceCallPersister javaIntefaceCallPersister, String clientIdentifier, String className, String methodName, Object[] args, Object result, InterfaceRecorderConfig interfaceRecorderConfig) {
        this.clientIdentifier = clientIdentifier;
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
            final ClassSerializer classSerializer = new ClassSerializer();
            final String parameters = classSerializer.asString(args);

            String resultStr = classSerializer.asString(result);
            RecorderLog.debug("LogRunner: Calling report callback");

            javaIntefaceCallPersister.event(new JavaInterfaceCall(clientIdentifier, className, methodName, parameters, resultStr));
        } catch (Throwable e) {
            RecorderLog.debug("LogRunner: exception logging " + e);
        }
    }

    @Override
    public void run() {
        if (interfaceRecorderConfig.getAsyncMode() == AsyncMode.ALL_ASYNC && !doLog()) {
            return;
        }
        logEvent();
    }

    public static void store(JavaIntefaceCallPersister javaIntefaceCallPersister, String clientIdentifier, String className, String methodName, Object[] args, Object result, InterfaceRecorderConfig interfaceRecorderConfig) {
        MethodCallStorageRunner methodCallStorageRunner = new MethodCallStorageRunner(javaIntefaceCallPersister, clientIdentifier, className, methodName, args, result, interfaceRecorderConfig);
        AsyncMode asyncMode = interfaceRecorderConfig.getAsyncMode();
        if (asyncMode == AsyncMode.ALL_ASYNC) {
            executorService.submit(methodCallStorageRunner);
            return;
        }
        if (methodCallStorageRunner.doLog()) {
            if (asyncMode == AsyncMode.ALL_SYNC) {
                methodCallStorageRunner.logEvent();
            } else {
                executorService.submit(methodCallStorageRunner);
            }
        }
    }

}
