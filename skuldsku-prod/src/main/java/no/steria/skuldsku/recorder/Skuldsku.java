package no.steria.skuldsku.recorder;

import no.steria.skuldsku.recorder.dbrecorder.DatabaseRecorder;
import no.steria.skuldsku.recorder.javainterfacerecorder.InstantiationCallback;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.InterfaceRecorderWrapper;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.MockRegistration;
import no.steria.skuldsku.recorder.logging.RecorderLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class for controlling recording and mocking.
 *
 * @see Skuldsku#initialize(SkuldskuConfig)
 */
public final class Skuldsku {

    private Skuldsku() {
        // avoid instantiation
    }

    private static SkuldskuConfig config = null;
    private static boolean recordingOn = false;
    private static List<DatabaseRecorder> databaseRecorders = new ArrayList<>();


    /**
     * Initializes <code>Skuldsku</code>. This method should be called before
     * any other method on this class, and can only be called once.
     *
     * @param config The configuration.
     */
    public static void initialize(SkuldskuConfig config) {
        if (Skuldsku.config != null) {
            RecorderLog.error("You are initializing Skuldsku twice, ignoring second attempt at initalizing.", new Throwable());
        } else {
            Skuldsku.config = new SkuldskuConfig(config);
            initializeDatabaseRecorders(config.getDatabaseRecorders());
        }
    }

    /**
     * Only used for testing: Resets Skuldsku to its initial state.
     */
    static void reset() {
        config = null;
        recordingOn = false;
        databaseRecorders = new ArrayList<>();
    }

    /**
     * Returns the <code>SkuldskuConfig</code>.
     * @return A copy of the <code>SkuldskuConfig</code> that was passed
     *          into {@link #initialize(SkuldskuConfig)}.
     */
    public static SkuldskuConfig getSkuldskuConfig() {
        return new SkuldskuConfig(config);
    }

    private static void assertInitialized() {
        if (config == null) {
            RecorderLog.error("You must call initialize() before you can start using Skuldsku. No recording " +
                    "will be done.", new Throwable());

        }
    }

    /**
     * Checks if Skuldsku is in "playback mode".
     *
     * @return <code>true</code> if the JVM property <code>no.steria.skuldsku.doMock</code>
     *          is set to "true", and <code>false</code> otherwise.
     */
    public static boolean isInPlayBackMode() {
        return "true".equals(System.getProperty("no.steria.skuldsku.doMock"));
    }

    /**
     * Checks if recording is enabled.
     * @return <code>true</code> if {@link #start()} has been called
     *          without a subsequent call to {@link #stop()}.
     */
    public static boolean isRecordingOn() {
        return recordingOn;
    }

    static void initializeDatabaseRecorders(List<DatabaseRecorder> databaseRecorders) {
        assertInitialized();
        Skuldsku.databaseRecorders = databaseRecorders;
        for (DatabaseRecorder databaseRecorder : databaseRecorders) {
            databaseRecorder.initialize();
        }
    }

    /**
     * Wraps the object given by the {@link InstantiationCallback#create() callback}
     * in order to support both recording and mocking. The latter is only activated
     * if {@link #isInPlayBackMode() in playback mode}.
     *
     * This method should be used for decorating every interface you want
     * to mock out in playback mode.
     *
     * @param clazz The java interface.
     * @param ic A callback for instantiating the implementation. The callback
     *          is used in order to avoid instantiating the object when it
     *          should be mocked out in playback.
     * @return A decorated version of the object given through the
     *          <code>InstantiationCallback</code>.
     */
    public static <T> T wrap(Class<T> clazz, InstantiationCallback<T> ic) {
        final T service;
        if (isInPlayBackMode()) {
            service = MockRegistration.getMock(clazz);
        } else {
            service = ic.create();
        }

        try {
            return InterfaceRecorderWrapper.newInstance(service, clazz, config.getJavaIntefaceCallPersister(), config.getInterfaceRecorderConfig());
        } catch (Exception e) {
            if (isInPlayBackMode()) {
                throw new RuntimeException("Wrapper initialization failed", e);
            } else {
                RecorderLog.error("Wrapper initialization failed: Returning service without wrapper", e);
                return ic.create();
            }
        }
    }

    /**
     * Starts recording data.
     *
     * @see #isRecordingOn()
     * @see #stop()
     */
    public static void start() {
        assertInitialized();
        if (!isRecordingOn()) {
            for (DatabaseRecorder dbRecorder : databaseRecorders) {
                dbRecorder.start();
            }
            recordingOn = true;
        }
    }

    /**
     * Stops recording data.
     *
     * @see #isRecordingOn()
     * @see #start()
     */
    public static void stop() {
        assertInitialized();
        if (isRecordingOn()) {
            for (DatabaseRecorder dbRecorder : databaseRecorders) {
                dbRecorder.stop();
            }
            recordingOn = false;
        }
    }

}
