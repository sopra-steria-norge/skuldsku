package no.steria.copito.spytest.spy;

import java.lang.reflect.Method;

public interface MockInterface {
    public Object invoke(Class<?> interfaceClass,Object serviceObject,Method method,Object[] args);
}
