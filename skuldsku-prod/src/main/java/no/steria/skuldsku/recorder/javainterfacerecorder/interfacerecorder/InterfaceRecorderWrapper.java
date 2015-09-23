package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.common.ClientIdentifierHolder;
import no.steria.skuldsku.recorder.logging.RecorderLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * This class is both a proxy factory and an invocation handler. The proxies manufactured will all use this class as
 * invocation handler for all method calls.
 */
public class InterfaceRecorderWrapper implements java.lang.reflect.InvocationHandler {
    private final Object obj;
    private final JavaIntefaceCallPersister javaIntefaceCallPersister;
    private final InterfaceRecorderConfig interfaceRecorderConfig;

    
    private InterfaceRecorderWrapper(Object obj, JavaIntefaceCallPersister javaIntefaceCallPersister, InterfaceRecorderConfig interfaceRecorderConfig) {
        this.obj = obj;
        this.javaIntefaceCallPersister = javaIntefaceCallPersister;
        this.interfaceRecorderConfig = interfaceRecorderConfig;
    }
    

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RecorderLog.debug("IRW: Invoke called for " + method.getName());
        Object result = null;
        try {
           result = method.invoke(obj, args);
           return result;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } finally {
            if (Skuldsku.isRecordingOn()) {
                storeMethodCall(method, args, result);
            }

        }
    }

    private void storeMethodCall(Method method, Object[] args, Object result) {
        try {
            final String className = determineClassName(obj);
            final String methodName = method.getName();
            
            MethodCallStorageRunner.store(javaIntefaceCallPersister, ClientIdentifierHolder.getClientIdentifier(), className, methodName, args, result, interfaceRecorderConfig);
            RecorderLog.info("IRW: Recorded for " + className + "," + methodName);
        } catch (Exception e) {
            RecorderLog.warn("IRW: Exception recording result : " + e);
        }
    }

    private static String determineClassName(Object obj) {
        final String className;
        if (obj instanceof Proxy) {
            className = ((MockInvocationHandler) Proxy.getInvocationHandler(obj)).getImplementationClass();
        } else {
            className = obj.getClass().getName();
        }
        return className;
    }
    
    
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Object obj, Class<T> givenInterface, JavaIntefaceCallPersister javaIntefaceCallPersister, InterfaceRecorderConfig interfaceRecorderConfig) {
        RecorderLog.info("IRW: Setup with " + givenInterface);
        InterfaceRecorderWrapper invocationHandler = new InterfaceRecorderWrapper(obj, javaIntefaceCallPersister, interfaceRecorderConfig);
        Object o = Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class<?>[]{givenInterface}, invocationHandler);
        return (T) o;
    }
}
