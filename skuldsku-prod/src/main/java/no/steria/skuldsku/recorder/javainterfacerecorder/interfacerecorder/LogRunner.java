package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;


import no.steria.skuldsku.recorder.Recorder;
import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogRunner implements Runnable {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final ReportCallback reportCallback;
    private final String className;
    private final String methodName;
    private final Object[] args;
    private final Object result;
    private final InterfaceRecorderConfig interfaceRecorderConfig;

    private LogRunner(ReportCallback reportCallback, String className, String methodName, Object[] args, Object result, InterfaceRecorderConfig interfaceRecorderConfig) {
        this.reportCallback = reportCallback;
        this.className = className;
        this.methodName = methodName;
        this.args = args;
        this.result = result;
        this.interfaceRecorderConfig = interfaceRecorderConfig;
    }

    private boolean doLog() {
        return Recorder.recordingIsOn();
    }

    private void logEvent() {
        RecorderDebugLogger logger = interfaceRecorderConfig.debugLogger();
        logger.debug("LogRunner: log event " + className + "," + methodName);

        try {
            ClassSerializer classSerializer = new ClassSerializer();
            StringBuilder parameters=new StringBuilder();
            if (args != null) {
                boolean first = true;
                for (Object para : args) {
                    String parStr = para != null ? para.getClass().getName() : null;
                    logger.debug("LogRunner: writing para " + parStr);
                    Object toLog = logObject(para);
                    if (!first) {
                        parameters.append(";");
                    }
                    first = false;
                    parameters.append(classSerializer.asString(toLog));
                }
            }
                logger.debug("LogRunner: writing result");
            String resultStr = classSerializer.asString(result);
            logger.debug("LogRunner: Calling report callback");
            reportCallback.event(className, methodName, parameters.toString(), resultStr);
        } catch (Throwable e) {
            logger.debug("LogRunner: exeption logging " + e);
        } finally {
            logger.debug("LogRunner: Leaving");
        }
    }

    private Object logObject(Object para) {
        if (interfaceRecorderConfig.isIgnored(className,methodName,para)) {
            return null;
        }
        return para;
    }


    public static void log(ReportCallback reportCallback, String className, String methodName, Object[] args, Object result, InterfaceRecorderConfig interfaceRecorderConfig) {
        LogRunner logRunner = new LogRunner(reportCallback, className, methodName, args, result, interfaceRecorderConfig);
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
