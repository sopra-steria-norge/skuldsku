package no.steria.skuldsku.recorder.java.mock;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import no.steria.skuldsku.common.result.Results;
import no.steria.skuldsku.common.result.ResultsProvider;
import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.java.JavaCall;
import no.steria.skuldsku.recorder.java.recorder.InterfaceRecorderWrapper;
import no.steria.skuldsku.recorder.logging.RecorderLog;

/**
 * Each Java API that should be mocked when playing back tests, has a corresponding proxy (created by
 * {@link InterfaceRecorderWrapper}).
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
    
    public static Results getResultsFromMocks() {
        Results results = new Results();
        for (MockInterface mi : mocks.values()) {
            if (mi instanceof ResultsProvider) {
                ResultsProvider p = (ResultsProvider) mi;
                results = Results.combine(results, p.getResults());
            }
        }
        
        return results;
    }

    private static MockInterface getMockInterface(Class<?> givenInterface) {
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
                new MockInvocationHandler(mi, interfaceClass));
        return service;
    }

    private static <T> MockInterface getMockInterfaceSafely(Class<T> interfaceClass) {
        MockInterface mi = getMockInterface(interfaceClass);

        if (mi == null && Skuldsku.isInPlayBackMode()) {
            mi = new MockInterface() {
                @Override
                public Object invoke(Class<?> interfaceClass, String serviceObjectName, Method method, Object[] args) {
                    final String message = String.format("Method %s called on mock interface %s during playbackmode, but mock interface was not registered", method.getName(), interfaceClass.getName());
                    RecorderLog.warn(message);
                    throw new IllegalStateException(message);
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
    
    
    public static void registerMocksFrom(String filename) {
        final List<JavaCall> javaCalls = JavaCall.readJavaInterfaceCalls(filename);
        registerMocksFrom(javaCalls);
    }
    
    public static void registerMocksFrom(List<JavaCall> javaCalls) {
        try {
            final List<RecordedDataMock> recordedDataMocks = prepareMocks(javaCalls);

            for (RecordedDataMock recordedDataMock: recordedDataMocks){
                final Class<?> mockClass = Class.forName(recordedDataMock.getServiceClass());
                for (Class<?> interfaceClass : mockClass.getInterfaces()) {
                    RecorderLog.debug("Registering mock on: " + interfaceClass.getName());
                    MockRegistration.registerMock(interfaceClass, recordedDataMock);
                }
                RecorderLog.debug("Registering mock on: " + mockClass.getName());
                MockRegistration.registerMock(mockClass, recordedDataMock);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static List<RecordedDataMock> prepareMocks(List<JavaCall> javaCalls) {
        Map<String, List<JavaCall>> recordingByService = groupByClassName(javaCalls);
        List<RecordedDataMock> recordedDataMocks = createRecordedDataMocks(recordingByService);
        return recordedDataMocks;
    }
    
    private static Map<String, List<JavaCall>> groupByClassName(List<JavaCall> callbacks) {
        Map<String, List<JavaCall>> recordings = new HashMap<>();
        for (JavaCall callback: callbacks) {
            List<JavaCall> recordObjects = recordings.get(callback.getClassName());
            if (recordObjects == null) {
                recordObjects = new ArrayList<>();
                recordings.put(callback.getClassName(), recordObjects);
            }
            recordObjects.add(callback);
        }

        return recordings;
    }
    
    private static List<RecordedDataMock> createRecordedDataMocks(Map<String, List<JavaCall>> recordingByService) {
        final List<RecordedDataMock> recordedDataMocks = new ArrayList<>();
        for (String serviceClass : recordingByService.keySet()) {
            RecordedDataMock recordedDataMock = new RecordedDataMock(recordingByService.get(serviceClass));
            recordedDataMock.setServiceClass(serviceClass);
            recordedDataMocks.add(recordedDataMock);
        }
        return recordedDataMocks;
    }
}
