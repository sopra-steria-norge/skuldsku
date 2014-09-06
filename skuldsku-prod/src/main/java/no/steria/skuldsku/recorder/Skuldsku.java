package no.steria.skuldsku.recorder;

import java.util.List;

import no.steria.skuldsku.recorder.dbrecorder.DatabaseRecorder;
import no.steria.skuldsku.recorder.javainterfacerecorder.InstantiationCallback;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.InterfaceRecorderWrapper;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.MockRegistration;

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
    private static List<DatabaseRecorder> databaseRecorders;

    
    /**
     * Initializes <code>Skuldsku</code>. This method should be called before
     * any other method on this class, and can only be called once.
     * 
     * @param config The configuration.
     */
    public static void initialize(SkuldskuConfig config) {
        if (Skuldsku.config != null) {
            throw new IllegalStateException("Already initialized: This is the second time the initialize method gets called.");
        }
        Skuldsku.config = new SkuldskuConfig(config);
        initializeDatabaseRecorders(config.getDatabaseRecorders());
    }
    
    /**
     * Only used for testing: Resets Skuldsku to its initial state.
     * @see SkuldskuAccessor#reset()
     */
    static void reset() {
        config = null;
        recordingOn = false;
        databaseRecorders = null;
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
            throw new IllegalStateException("Skuldsku.initialize should be called before any other method.");
        }
    }
    
    /**
     * Checks if Skuldsku is in "playback mode".
     * 
     * @return <code>true</code> if the JVM property <code>no.steria.skuldsku.doMock</code>
     *          is set to "true", and <code>false</code> otherwise.
     */
    public static boolean isInPlayBackMode() {
        assertInitialized();
        return "true".equals(System.getProperty("no.steria.skuldsku.doMock"));
    }
    
    /**
     * Checks if recording is enabled.
     * @return <code>true</code> if {@link #start()} has been called
     *          without a subsequent call to {@link #stop()}.
     */
    public static boolean isRecordingOn() {
        assertInitialized();
        return recordingOn;
    }

    @Deprecated
    public static void initializeDatabaseRecorders(List<DatabaseRecorder> databaseRecorders) {
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
        assertInitialized();
        
        final T service;
        if (isInPlayBackMode()) {
            service = MockRegistration.getMock(clazz);
        } else {
            service = ic.create();
        }

        /*
         * TODO: Ved sammenlikning vil vi helt sikkert ønske å sjekke implementasjonsklasse. Ved opptak
         * mot mock vil vi ha en annen type enn ved normal kjøring. Implementasjonsklasse bør derfor hentes ut
         * av mocken og sendes med som parameter til InterfaceRecorderWrapper fremfor å bruke getClass(). Bør
         * skrive en test her for å sikre at mock-output og vanlig impl-output blir likt :-)
         */
        return InterfaceRecorderWrapper.newInstance(service, clazz, config.getReportCallback(), config.getInterfaceRecorderConfig());
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
