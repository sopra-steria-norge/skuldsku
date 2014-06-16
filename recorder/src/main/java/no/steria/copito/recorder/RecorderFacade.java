package no.steria.copito.recorder;

import net.jcip.annotations.NotThreadSafe;
import no.steria.copito.recorder.dbrecorder.DatabaseRecorder;
import no.steria.copito.recorder.httprecorder.CallReporter;
import no.steria.copito.recorder.httprecorder.ServletFilter;
import no.steria.copito.recorder.javainterfacerecorder.interfacerecorder.MockInterface;
import no.steria.copito.recorder.javainterfacerecorder.interfacerecorder.MockRegistration;
import no.steria.copito.recorder.javainterfacerecorder.serializer.ClassSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        try (PrintWriter printWriter = new PrintWriter(file)) {
            writeDatabaseDataToFile(printWriter);
            writeJavaApiRecordingsToFile(printWriter);
            writeHttpRecordingsToFile(printWriter);
        }
    }

    void resetFilterRegister() {
        servletFilters.clear();
    }


    private void writeHttpRecordingsToFile(PrintWriter printWriter) throws FileNotFoundException {
        for (ServletFilter servletFilter : servletFilters) {
            CallReporter reporter = servletFilter.getReporter();

            printWriter.write(reporter.getRecordedData());
        }
        printWriter.flush();
    }

    private void writeJavaApiRecordingsToFile(PrintWriter printWriter) {
        ClassSerializer serializer = new ClassSerializer();
        MockRegistration mockRegistration = new MockRegistration();
        Iterator<Map.Entry<Class<?>, MockInterface>> iterator = mockRegistration.getIterator();
        while (iterator.hasNext()) {
            Map.Entry<Class<?>, MockInterface> mockInterfaceEntry = iterator.next();
            String mockObjectAsString = serializer.asString(mockInterfaceEntry.getValue());
            printWriter.print(mockObjectAsString);
        }
        printWriter.flush();
    }

    private void writeDatabaseDataToFile(PrintWriter printWriter) throws FileNotFoundException {
        for (DatabaseRecorder databaseRecorder : databaseRecorders) {
            databaseRecorder.exportTo(printWriter);
        }
        printWriter.flush();
    }
}
