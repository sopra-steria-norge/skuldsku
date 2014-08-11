package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

import no.steria.skuldsku.recorder.Recorder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * This class is both a proxy factory and an invocation handler. The proxies manufactured will all use this class as
 * invocation handler for all method calls.
 */
public class InterfaceRecorderWrapper implements java.lang.reflect.InvocationHandler {
    private static InterfaceRecorderConfig interfaceRecorderConfig;
    private final Object obj;
    private final ReportCallback reportCallback;
    private final Class<?> givenInterface;

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Object obj, Class<T> givenInterface, ReportCallback reportCallback, InterfaceRecorderConfig interfaceRecorderConfig) {
        interfaceRecorderConfig.debugLogger().debug("IRW: Setup with " + givenInterface);
        InterfaceRecorderWrapper.interfaceRecorderConfig = interfaceRecorderConfig;
        InterfaceRecorderWrapper invocationHandler = new InterfaceRecorderWrapper(obj,reportCallback,givenInterface);
        Object o = Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class<?>[]{givenInterface}, invocationHandler);
        return (T) o;
    }

    private InterfaceRecorderWrapper(Object obj, ReportCallback reportCallback, Class<?> givenInterface) {
        this.obj = obj;
        this.reportCallback = reportCallback;
        this.givenInterface = givenInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RecorderDebugLogger logger = interfaceRecorderConfig.debugLogger();
        logger.debug("IRW: Invoke called for " + method.getName());
        Object result = null;
        MockInterface mock = MockRegistration.getMock(obj.getClass());
        try {
            if (mock != null) {
                result = mock.invoke(givenInterface, obj, method, args);
            } else {
                result = method.invoke(obj, args);
            }
            return result;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " +
                    e.getMessage());
        } finally {
            if (Recorder.recordingIsOn()) {
                try {
                    logger.debug("IRW: Logging..");
                    String className = obj.getClass().getName();
                    String methodName = method.getName();
                    logger.debug("IRW: Logging for " + className + "," + methodName);
                    LogRunner.log(reportCallback, className, methodName, args, result, interfaceRecorderConfig);
                } catch (Exception e) {
                    logger.debug("IRW: Exception loggin result : " + e);
                }
            }

        }
    }
}
