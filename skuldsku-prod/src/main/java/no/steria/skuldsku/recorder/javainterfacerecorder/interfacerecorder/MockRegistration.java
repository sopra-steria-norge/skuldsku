package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import no.steria.skuldsku.recorder.Skuldsku;

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
        return Skuldsku.isInPlayBackMode() ? mocks.get(givenInterface) : null;
    }
    
    public static <T> T getMock(final Class<T> interfaceClass) {
        final MockInterface mi = getMockInterface(interfaceClass);
        
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
