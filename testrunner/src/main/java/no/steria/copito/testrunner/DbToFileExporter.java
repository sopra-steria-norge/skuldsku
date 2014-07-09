package no.steria.copito.testrunner;

import no.steria.copito.recorder.logging.RecorderLog;
import no.steria.copito.utils.SimpleTransactionManager;
import no.steria.copito.utils.TransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class DbToFileExporter {

    public static final int ANT_COLUMNS_DATABASE_RECORDINGS = 7;
    public static final int ANT_COLUMNS_JAVA_INTERFACE_RECORDINGS = 7;
    public static final int ANT_COLUMNS_HTTP_RECORDINGS = 5;

    /**
     * After the recording of data, this method can be used to extract the data and write it to file,
     * so that it can be used for running tests.
     *
     * @param databaseRecordingsTable      The name of the table that contains the database recordings
     * @param javaInterfaceRecordingsTable The name of the table that contains the java interface recordings
     * @param httpRecordingsTable          The name of the table that contains the http recordings
     * @param dataSource                   The data source for the database that contains the tables
     */
    public static void exportTo(OutputStream os, String databaseRecordingsTable, String javaInterfaceRecordingsTable,
                                String httpRecordingsTable, DataSource dataSource) {
        TransactionManager transactionManager = new SimpleTransactionManager(dataSource);
        PrintWriter printWriter = new PrintWriter(os);
        printWriter.write("\n **DATABASE RECORDINGS** \n\n");
        exportRecordingsToFile(printWriter, databaseRecordingsTable, ANT_COLUMNS_DATABASE_RECORDINGS, transactionManager);
        printWriter.write("\n **JAVA INTERFACE RECORDINGS** \n\n");
        exportRecordingsToFile(printWriter, javaInterfaceRecordingsTable, ANT_COLUMNS_JAVA_INTERFACE_RECORDINGS, transactionManager);
        printWriter.write("\n **HTTP RECORDINGS** \n\n");
        exportRecordingsToFile(printWriter, httpRecordingsTable, ANT_COLUMNS_HTTP_RECORDINGS, transactionManager);
        try {
            os.flush();
        } catch (IOException e) {
            RecorderLog.error("Could not export data to file.", e);
        }

    }

    private static void exportRecordingsToFile(final PrintWriter printWriter, String table, int antColumns, TransactionManager transactionManager) {
        transactionManager.doInTransaction(jdbc -> {
            jdbc.query("SELECT * FROM " + table, rs -> {
                while (rs.next()) {
                    for (int i = 1; i < antColumns; i++) {
                        String data = rs.getString(i);
                        printWriter.write("\"" + (data == null ? "" : data) + "\",");
                    }
                    String data = rs.getString(antColumns);
                    printWriter.write("\"" + (data == null ? "" : data) + "\";");
                }
                printWriter.write("\n");
                printWriter.flush();
            });
            return null;
        });
    }
}
