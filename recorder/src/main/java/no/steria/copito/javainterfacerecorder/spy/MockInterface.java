package no.steria.copito.javainterfacerecorder.spy;

import java.lang.reflect.Method;

public interface MockInterface {
    public Object invoke(Class<?> interfaceClass,Object serviceObject,Method method,Object[] args);
}
