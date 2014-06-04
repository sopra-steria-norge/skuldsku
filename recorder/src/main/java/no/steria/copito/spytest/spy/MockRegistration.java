package no.steria.copito.spytest.spy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MockRegistration {
    private static Map<Class<?>,Object> mocks = new HashMap<>();
    public static <T> void registerMock(Class<T> mockClass, T mock) {
        mocks.put(mockClass,mock);
    }

    public static <T> T getMock(Class<T> givenInterface) {
        if (!"true".equals(System.getProperty("no.steria.copito.doMock","false"))) {
            return null;
        }
        T mock = (T) mocks.get(givenInterface);
        return mock;
    }
}
