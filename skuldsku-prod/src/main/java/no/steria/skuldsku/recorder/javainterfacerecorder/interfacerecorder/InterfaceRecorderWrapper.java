package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.logging.RecorderLog;

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
    private final JavaIntefaceCallPersister javaIntefaceCallPersister;

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Object obj, Class<T> givenInterface, JavaIntefaceCallPersister javaIntefaceCallPersister, InterfaceRecorderConfig interfaceRecorderConfig) {
        RecorderLog.debug("IRW: Setup with " + givenInterface);
        InterfaceRecorderWrapper.interfaceRecorderConfig = interfaceRecorderConfig;
        InterfaceRecorderWrapper invocationHandler = new InterfaceRecorderWrapper(obj, javaIntefaceCallPersister);
        Object o = Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class<?>[]{givenInterface}, invocationHandler);
        return (T) o;
    }

    private InterfaceRecorderWrapper(Object obj, JavaIntefaceCallPersister javaIntefaceCallPersister) {
        this.obj = obj;
        this.javaIntefaceCallPersister = javaIntefaceCallPersister;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RecorderLog.debug("IRW: Invoke called for " + method.getName());
        Object result = null;
        try {
           result = method.invoke(obj, args);
           return result;
        } catch (InvocationTargetException e) { //TODO ikh: is this necessary?
            throw e.getTargetException();
        } finally {
            if (Skuldsku.isRecordingOn()) {
                try {
                    String className = obj.getClass().getName();
                    String methodName = method.getName();
                    LogRunner.log(javaIntefaceCallPersister, className, methodName, args, result, interfaceRecorderConfig);
                    RecorderLog.debug("IRW: Logged for " + className + "," + methodName);
                } catch (Exception e) {
                    RecorderLog.debug("IRW: Exception loggin result : " + e);
                }
            }

        }
    }
}
