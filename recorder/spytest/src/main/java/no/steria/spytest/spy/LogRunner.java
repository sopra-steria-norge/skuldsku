package no.steria.spytest.spy;

import no.steria.spytest.serializer.ClassSerializer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogRunner implements Runnable {
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final ReportCallback reportCallback;
    private final String className;
    private final String methodName;
    private final Object[] args;
    private final Object result;
    private final AsyncMode asyncMode;

    private LogRunner(ReportCallback reportCallback, String className, String methodName, Object[] args, Object result, AsyncMode asyncMode) {
        this.reportCallback = reportCallback;
        this.className = className;
        this.methodName = methodName;
        this.args = args;
        this.result = result;
        this.asyncMode = asyncMode;
    }

    private boolean doLog() {
        if (!reportCallback.doReport()) {
            return false;
        }
        return reportCallback.doReport(className,methodName);
    }

    private void logEvent() {
        ClassSerializer classSerializer = new ClassSerializer();
        StringBuilder parameters=new StringBuilder();
        if (args != null) {
            boolean first = true;
            for (Object para : args) {
                if (!first) {
                    parameters.append(";");
                }
                first = false;
                parameters.append(classSerializer.asString(para));
            }
        }
        String resultStr = classSerializer.asString(result);
        reportCallback.event(className,methodName,parameters.toString(),resultStr);
    }



    public static void log(ReportCallback reportCallback, String className, String methodName, Object[] args, Object result, AsyncMode asyncMode) {
        LogRunner logRunner = new LogRunner(reportCallback, className, methodName, args, result, asyncMode);

        if (asyncMode == AsyncMode.ALL_ASYNC) {
            executorService.submit(logRunner);
            return;
        }
        if (!logRunner.doLog()) {
            return;
        }
        if (asyncMode == AsyncMode.ALL_SYNC) {
            logRunner.logEvent();
        } else {
            executorService.submit(logRunner);
        }
    }

    @Override
    public void run() {
        if (asyncMode == AsyncMode.ALL_ASYNC && !doLog()) {
            return;
        }
        logEvent();
    }
}
