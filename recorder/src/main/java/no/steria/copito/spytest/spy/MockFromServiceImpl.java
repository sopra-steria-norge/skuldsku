package no.steria.copito.spytest.spy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MockFromServiceImpl implements MockInterface {

    private Object mockService;

    public MockFromServiceImpl(Object mockService) {

        this.mockService = mockService;
    }

    public static <T> MockInterface create(Class<T> interfaceClass,T mockService) {
        return new MockFromServiceImpl(mockService);
    }

    @Override
    public Object invoke(Class<?> interfaceClass, Object serviceObject, Method method, Object[] args) {
        try {
            return method.invoke(mockService,args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
