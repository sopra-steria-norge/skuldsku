package no.steria.skuldsku.recorder.java.recorder;

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.common.ClientIdentifierHolder;
import no.steria.skuldsku.recorder.java.mock.MockInvocationHandler;
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
    private final JavaCallPersister javaCallPersister;
    private final JavaCallRecorderConfig javaCallRecorderConfig;

    
    private InterfaceRecorderWrapper(Object obj, JavaCallPersister javaCallPersister, JavaCallRecorderConfig javaCallRecorderConfig) {
        this.obj = obj;
        this.javaCallPersister = javaCallPersister;
        this.javaCallRecorderConfig = javaCallRecorderConfig;
    }
    

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RecorderLog.debug("IRW: Invoke called for " + method.getName());
        Object result = null;
        Throwable thrown = null;
        try {
           result = method.invoke(obj, args);
           return result;
        } catch (InvocationTargetException e) {
            thrown = e.getTargetException();
            throw thrown;
        } finally {
            if (Skuldsku.isRecordingOn()) {
                storeMethodCall(method, args, result, thrown);
            }
        }
    }

    private void storeMethodCall(Method method, Object[] args, Object result, Throwable thrown) {
        try {
            final String className = determineClassName(obj);
            final String methodName = method.getName();
            
            JavaCallPersisterRunner.store(javaCallPersister, ClientIdentifierHolder.getClientIdentifier(), className, methodName, args, result, thrown, javaCallRecorderConfig);
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
    public static <T> T newInstance(Object obj, Class<T> givenInterface, JavaCallPersister javaCallPersister, JavaCallRecorderConfig javaCallRecorderConfig) {
        RecorderLog.info("IRW: Setup with " + givenInterface);
        InterfaceRecorderWrapper invocationHandler = new InterfaceRecorderWrapper(obj, javaCallPersister, javaCallRecorderConfig);
        Object o = Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class<?>[]{givenInterface}, invocationHandler);
        return (T) o;
    }
}
