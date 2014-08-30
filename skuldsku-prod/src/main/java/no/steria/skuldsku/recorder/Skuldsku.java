package no.steria.skuldsku.recorder;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import no.steria.skuldsku.recorder.dbrecorder.DatabaseRecorder;
import no.steria.skuldsku.recorder.dbrecorder.impl.oracle.OracleDatabaseRecorder;

/**
 * Facade for starting and stopping all available recorders.
 */
public class Skuldsku {

    private Skuldsku() {
        // avoid instantiation
    }

    private static boolean recordingOn = false;

    private static List<DatabaseRecorder> databaseRecorders = new ArrayList<>();

    public static boolean isInPlayBackMode() {
        return "true".equals(System.getProperty("no.steria.skuldsku.doMock"));
    }

    /**
     * @param databaseRecorders new database recorders to be used. This will overwrite the previous ones.
     */
    @Deprecated
    public static void initializeDatabaseRecorders(List<DatabaseRecorder> databaseRecorders) {
        Skuldsku.databaseRecorders = databaseRecorders;
        for (DatabaseRecorder databaseRecorder : databaseRecorders) {
            databaseRecorder.initialize();
        }
    }
    
    public static void addDataSourceForRecording(DataSource dataSource) {
        // TODO: Update with driver detection when we have other implementations:
        final OracleDatabaseRecorder dbr = new OracleDatabaseRecorder(dataSource);
        dbr.initialize();
        databaseRecorders.add(dbr);
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
