package no.steria.copito.spytest.spy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MockRegistration {
    private static Map<Class<?>,MockInterface> mocks = new HashMap<>();
    public static void registerMock(Class<?> mockClass, MockInterface mock) {
        mocks.put(mockClass,mock);
    }

    public static MockInterface getMock(Class<?> givenInterface) {
        if (!"true".equals(System.getProperty("no.steria.copito.doMock","false"))) {
            return null;
        }

        return mocks.get(givenInterface);
    }
}
