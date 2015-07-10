package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.logging.RecorderLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Each Java API that should be mocked when playing back tests, has a corresponding proxy (created by
 *
 * @link InterfaceRecorderWrapper).
 * When skuldsku is in playback mode, it will attempt to call the corresponding mock to handle the call. It will look up
 * the mock from this class, based on the interface
 */
public class MockRegistration {
    private static Map<Class<?>, MockInterface> mocks = new HashMap<>();
    private static boolean initialized = false;
    private final static Lock lock = new ReentrantLock();
    private final static Condition notInitialized = lock.newCondition();


    public static void waitForInitialization() {
        if (!initialized) {
            lock.lock();
            try {
                notInitialized.await();
                initialized = true;
            } catch (InterruptedException e) {
                e.printStackTrace(); // Should only happen on shutdown
            } finally {
                lock.unlock();
            }
        }
    }

    public static void registerMock(Class<?> mockClass, MockInterface mock) {
        mocks.put(mockClass, mock);
        signal();
    }

    @Deprecated
    public static MockInterface getMockInterface(Class<?> givenInterface) {
        final MockInterface mockInterface = mocks.get(givenInterface);

        if (Skuldsku.isInPlayBackMode() && mockInterface == null) {
            RecorderLog.warn(String.format("Mock <%s> interface not registered during playback mode.", givenInterface.getSimpleName()));
        }
        return Skuldsku.isInPlayBackMode() ? mockInterface : null;
    }

    public static <T> T getMock(final Class<T> interfaceClass) {
        final MockInterface mi = getMockInterfaceSafely(interfaceClass);

        @SuppressWarnings("unchecked")
        T service = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] {interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return mi.invoke(interfaceClass, interfaceClass.getName(), method, args); // interfaceClass.getName() ?
                    }
                });
        return service;
    }

    private static <T> MockInterface getMockInterfaceSafely(Class<T> interfaceClass) {
        MockInterface mi = getMockInterface(interfaceClass);

        if (mi == null && Skuldsku.isInPlayBackMode()) {
            mi = new MockInterface() {
                @Override
                public Object invoke(Class<?> interfaceClass, String serviceObjectName, Method method, Object[] args) {
                    RecorderLog.warn(String.format("Method %s called on mock interface %s during playbackmode, but mock interface was not registered", method.getName(), interfaceClass.getName()));
                    return null;
                }
            };
        }

        return mi;
    }

    public static void reset() {
        mocks = new HashMap<>();
    }

    public static void registerMocks(Map<Class<?>, MockInterface> newMocks) {
        mocks = newMocks;
        signal();
    }

    private static void signal() {
        if (!initialized) {
            lock.lock();
            try {
                notInitialized.signalAll();
                initialized = true;
            } finally {
                lock.unlock();
            }
        }
    }
}
