package no.steria.copito.recorder;

import net.jcip.annotations.NotThreadSafe;
import no.steria.copito.recorder.dbrecorder.DatabaseRecorder;
import no.steria.copito.recorder.httprecorder.CallReporter;
import no.steria.copito.recorder.httprecorder.ServletFilter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Facade for starting and stopping all available recorders.
 */
@NotThreadSafe
public class RecorderFacade {
    private static boolean recordingOn = false;

    List<DatabaseRecorder> databaseRecorders;

    private static final List<ServletFilter> servletFilters = new ArrayList<>();

    public RecorderFacade(@NotNull List<DatabaseRecorder> databaseRecorders) {
        this.databaseRecorders = databaseRecorders;
    }

    public static boolean recordingIsOn() {
        return recordingOn;
    }

    public static void registerFilter(ServletFilter servletFilter) {
        servletFilters.add(servletFilter);
    }

    public void start() throws SQLException {
        if (!recordingIsOn()) {
            for (DatabaseRecorder dbRecorder : databaseRecorders) {
                dbRecorder.start();
            }
            recordingOn = true;
        }
    }

    public void stop() {
        if (recordingIsOn()) {
            for (DatabaseRecorder dbRecorder : databaseRecorders) {
                dbRecorder.stop();
            }
            recordingOn = false;
        }
    }

    public void exportTo(File file) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        exportTo(outputStream);
    }

    void exportTo(OutputStream outputStream) {
        try(OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
            writeDatabaseDataTo(outputStreamWriter);
            writeJavaApiRecordingsTo(outputStreamWriter);
            writeHttpRecordingsTo(outputStreamWriter);
        } catch (IOException ioe){
            //TODO ikh: log stuff!
            ioe.printStackTrace();
        }
    }

    void resetFilterRegister() {
        servletFilters.clear();
    }


    private void writeHttpRecordingsTo(OutputStreamWriter writer) throws IOException {
        for (ServletFilter servletFilter : servletFilters) {
            CallReporter reporter = servletFilter.getReporter();
            try {
                writer.write(reporter.getRecordedData());
            } catch (IOException e) {
                //TODO: ikh: logg stuff!
                e.printStackTrace();
            }
        }
        writer.flush();
    }

    private void writeJavaApiRecordingsTo(OutputStreamWriter writer) {
       // TODO: ikh: implement!
    }

    private void writeDatabaseDataTo    (OutputStreamWriter writer) throws IOException {
        PrintWriter printWriter = new PrintWriter(writer);
        for (DatabaseRecorder databaseRecorder : databaseRecorders) {
            databaseRecorder.exportTo(printWriter);
        }
        printWriter.flush();
        writer.flush();
    }
}
