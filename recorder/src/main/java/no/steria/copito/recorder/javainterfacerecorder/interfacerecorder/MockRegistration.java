package no.steria.copito.recorder.javainterfacerecorder.interfacerecorder;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Each proxy for recording Java APIs has a corresponding Mock, which is registered in the mocks Map
 * in this class. This way the data is registered on the mock itself, so that the mock can be played
 * back later. After recording, the mock will be serialized and saved to file, and before running a test, the mock
 * will be read back from file, and is thus ready for playing.
 */
public class MockRegistration {
    // todo: ikh: Assumes that only one interface/class of each type will need to be recorded?
    private static Map<Class<?>,MockInterface> mocks = new HashMap<>();
    public static void registerMock(Class<?> mockClass, MockInterface mock) {
        mocks.put(mockClass,mock);
    }

    @Nullable
    public static MockInterface getMock(Class<?> givenInterface) {
        if (!"true".equals(System.getProperty("no.steria.copito.doMock","false"))) {
            return null;
        }

        return mocks.get(givenInterface);
    }

    public static MockInterface removeMock(Class<?> givenInterface) {
        MockInterface remove = mocks.remove(givenInterface);
        return remove;
    }

    public Iterator<Map.Entry<Class<?>, MockInterface>> getIterator() {
        return mocks.entrySet().iterator();
    }
}
