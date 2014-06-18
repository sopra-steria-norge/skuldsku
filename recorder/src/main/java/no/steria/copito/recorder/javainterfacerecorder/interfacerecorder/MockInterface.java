package no.steria.copito.recorder.javainterfacerecorder.interfacerecorder;

import java.lang.reflect.Method;

/**
 * The mock Interface will be called from the proxies for the recorded APIs when in testrunning mode.
 * @see (no.steria.copito.recorder.javainterfacerecorder.interfacerecorder.MockRegistration).
 */
public interface MockInterface {
    public Object invoke(Class<?> interfaceClass,Object serviceObject,Method method,Object[] args);
}
