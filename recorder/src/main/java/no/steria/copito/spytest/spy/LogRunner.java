package no.steria.copito.spytest.spy;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import no.steria.copito.spytest.serializer.ClassSerializer;

public class LogRunner implements Runnable {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final ReportCallback reportCallback;
    private final String className;
    private final String methodName;
    private final Object[] args;
    private final Object result;
    private final SpyConfig spyConfig;

    private LogRunner(ReportCallback reportCallback, String className, String methodName, Object[] args, Object result, SpyConfig spyConfig) {
        this.reportCallback = reportCallback;
        this.className = className;
        this.methodName = methodName;
        this.args = args;
        this.result = result;
        this.spyConfig = spyConfig;
    }

    private boolean doLog() {
        return reportCallback.doReport() && reportCallback.doReport(className, methodName);
    }

    private void logEvent() {
        ClassSerializer classSerializer = new ClassSerializer();
        StringBuilder parameters=new StringBuilder();
        if (args != null) {
            boolean first = true;
            for (Object para : args) {
                Object toLog = logObject(para);
                if (!first) {
                    parameters.append(";");
                }
                first = false;
                parameters.append(classSerializer.asString(toLog));
            }
        }
        String resultStr = classSerializer.asString(result);
        reportCallback.event(className,methodName,parameters.toString(),resultStr);
    }

    private Object logObject(Object para) {
        if (spyConfig.isIgnored(className,methodName,para)) {
            return null;
        }
        return para;
    }


    public static void log(ReportCallback reportCallback, String className, String methodName, Object[] args, Object result, SpyConfig spyConfig) {
        LogRunner logRunner = new LogRunner(reportCallback, className, methodName, args, result, spyConfig);
        AsyncMode asyncMode = spyConfig.getAsyncMode();
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
        if (spyConfig.getAsyncMode() == AsyncMode.ALL_ASYNC && !doLog()) {
            return;
        }
        logEvent();
    }
}
