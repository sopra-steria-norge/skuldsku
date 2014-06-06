package no.steria.copito.recorder;

import no.steria.copito.recorder.dbrecorder.DatabaseRecorder;
import no.steria.copito.recorder.dbrecorder.impl.oracle.OracleDatabaseRecorder;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Facade for starting and stopping all available recorders.
 */
public class RecorderFacade {
    public static boolean servletFilterOn = false;
    DatabaseRecorder oracleDatabaseRecorder;

    public RecorderFacade(DataSource dataSource) {
        oracleDatabaseRecorder = new OracleDatabaseRecorder(dataSource);
    }

    public void start() throws SQLException {
        oracleDatabaseRecorder.start();
        servletFilterOn = true;
    }

    public void stop() {
        oracleDatabaseRecorder.stop();
        servletFilterOn = false;
    }
}
