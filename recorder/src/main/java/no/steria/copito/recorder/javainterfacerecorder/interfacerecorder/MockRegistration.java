package no.steria.copito.recorder.javainterfacerecorder.interfacerecorder;

import java.util.HashMap;
import java.util.Map;

/**
 * Each Java API that should be mocked when playing back tests, has a corresponding proxy (created by
 * @link no.steria.copito.recorder.javainterfacerecorder.interfacerecorder.InterfaceRecorderWrapper).
 * When Copito is in playback mode, it will attempt to call the corresponding mock to handle the call. It will look up
 * the mock from this class, based on the interface
 */
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
