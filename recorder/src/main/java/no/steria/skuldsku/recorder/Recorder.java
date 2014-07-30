package no.steria.skuldsku.recorder;

import no.steria.skuldsku.recorder.dbrecorder.DatabaseRecorder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Facade for starting and stopping all available recorders.
 */
public class Recorder {

    private Recorder() {
        // avoid instantiation
    }

    private static boolean recordingOn = false;

    private static List<DatabaseRecorder> databaseRecorders = new ArrayList<>();

    /**
     * @param databaseRecorders new database recorders to be used. This will overwrite the previous ones.
     */
    public static void initializeDatabaseRecorders(List<DatabaseRecorder> databaseRecorders) {
        Recorder.databaseRecorders = databaseRecorders;
        for (DatabaseRecorder databaseRecorder : databaseRecorders) {
            databaseRecorder.initialize();
        }
    }

    public static boolean recordingIsOn() {
        return recordingOn;
    }

    public static void start() {
        if (!recordingIsOn()) {
            for (DatabaseRecorder dbRecorder : databaseRecorders) {
                dbRecorder.start();
            }
            recordingOn = true;
        }
    }

    public static void stop() {
        if (recordingIsOn()) {
            for (DatabaseRecorder dbRecorder : databaseRecorders) {
                dbRecorder.stop();
            }
            recordingOn = false;
        }
    }

}
