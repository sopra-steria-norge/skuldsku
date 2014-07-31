package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This implementation of MockInterface delegates the handling of recording of interaction data to
 * the mock service provided in the constructor.
 */
public class MockFromServiceImpl<T> implements MockInterface {

    private final T mockService;

    private MockFromServiceImpl(T mockService) {

        this.mockService = mockService;
    }

    public static <T> MockInterface create(Class<T> interfaceClass,T mockService) {
        return new MockFromServiceImpl<>(mockService);
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
