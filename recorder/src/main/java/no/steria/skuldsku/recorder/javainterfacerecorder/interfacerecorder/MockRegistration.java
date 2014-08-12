package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

import no.steria.skuldsku.recorder.Recorder;

import java.util.HashMap;
import java.util.Map;

/**
 * Each Java API that should be mocked when playing back tests, has a corresponding proxy (created by
 *
 * @link InterfaceRecorderWrapper).
 * When skuldsku is in playback mode, it will attempt to call the corresponding mock to handle the call. It will look up
 * the mock from this class, based on the interface
 */
public class MockRegistration {
    private static Map<Class<?>, MockInterface> mocks = new HashMap<>();

    public static void registerMock(Class<?> mockClass, MockInterface mock) {
        mocks.put(mockClass, mock);
    }

    public static MockInterface getMock(Class<?> givenInterface) {
        return Recorder.isInPlayBackMode() ? mocks.get(givenInterface) : null;
    }

    public static void reset() {
        mocks = new HashMap<>();
    }
}
