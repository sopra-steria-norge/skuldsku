package no.steria.skuldsku.recorder.java.mock;

import java.lang.reflect.Method;

/**
 * The mock Interface will be called from the proxies for the recorded APIs when in testrunning mode.
 * @see (no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.MockRegistration).
 */
public interface MockInterface {
    Object invoke(Class<?> interfaceClass, String serviceObjectName, Method method, Object[] args);
}
