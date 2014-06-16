package no.steria.copito.recorder.javainterfacerecorder.interfacerecorder;

import java.lang.reflect.Method;

/**
 * For recording from Java APIs, the InterfaceRecorderWrapper creates proxies for the APIs in question, and for
 * each proxy the corresponding MockInterface implementation is called. The MockInterface implementation
 * takes care of the actual recording of the interaction.
 */
public interface MockInterface {
    public Object invoke(Class<?> interfaceClass,Object serviceObject,Method method,Object[] args);
}
