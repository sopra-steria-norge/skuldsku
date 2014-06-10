package no.steria.copito.recorder;

import no.steria.copito.recorder.dbrecorder.DatabaseRecorder;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

/**
 * Facade for starting and stopping all available recorders.
 */
public class RecorderFacade {
    private static boolean recordingOn = false;
    @Nullable
    DatabaseRecorder databaseRecorder;

    public RecorderFacade(@Nullable DatabaseRecorder databaseRecorder) {
        this.databaseRecorder = databaseRecorder;
    }

    public static boolean recordingIsOn() {
        return recordingOn;
    }

    public void start() throws SQLException {
            if (!recordingIsOn() && databaseRecorder != null) {
            databaseRecorder.start();
        }
        recordingOn = true;
    }

    public void stop() {
        if (recordingIsOn() && databaseRecorder != null) {
            databaseRecorder.stop();
            recordingOn = false;
        }
    }

}
