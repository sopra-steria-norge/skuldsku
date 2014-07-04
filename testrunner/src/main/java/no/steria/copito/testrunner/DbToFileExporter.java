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
    public static final int ANT_COLUMNS_INTERFACE_RECORDINGS = 7;
    public static final int ANT_COLUMNS_HTTP_RECORDINGS = 5;
    private final String databaseRecordingsTable;
    private final String interfaceRecordingsTable;
    private final String httpRecordingsTable;
    private final TransactionManager transactionManager;


    /**
     * After the recording of data, this method can be used to extract the data and write it to file,
     * so that it can be used for running tests.
     *
     * @param databaseRecordingsTable The name of the table that contains the database recordings
     * @param interfaceRecordingsTable The name of the table that contains the interface recordings
     * @param httpRecordingsTable The name of the table that contains the http recordings
     * @param dataSource The data source for the database that contains the tables
     */
    public DbToFileExporter(String databaseRecordingsTable, String interfaceRecordingsTable, String httpRecordingsTable,
                            DataSource dataSource) {
        this.databaseRecordingsTable = databaseRecordingsTable;
        this.interfaceRecordingsTable = interfaceRecordingsTable;
        this.httpRecordingsTable = httpRecordingsTable;
        this.transactionManager = new SimpleTransactionManager(dataSource);
    }

    public void exportTo(OutputStream os) {
        PrintWriter printWriter = new PrintWriter(os);
        printWriter.write(" **DATABASE RECORDINGS** ");
        exportRecordingsToFile(printWriter, databaseRecordingsTable, ANT_COLUMNS_DATABASE_RECORDINGS);
        printWriter.write(" **INTERFACE RECORDINGS** ");
        exportRecordingsToFile(printWriter, interfaceRecordingsTable, ANT_COLUMNS_INTERFACE_RECORDINGS);
        printWriter.write(" **HTTP RECORDINGS** ");
        exportRecordingsToFile(printWriter, httpRecordingsTable, ANT_COLUMNS_HTTP_RECORDINGS);
        try {
            os.flush();
        } catch (IOException e) {
            RecorderLog.error("Could not export data to file.", e);
        }

    }

    void exportRecordingsToFile(final PrintWriter printWriter, String table, int antColumns) {
        transactionManager.doInTransaction(jdbc -> {
            jdbc.query("SELECT * FROM " + table, rs -> {
                while (rs.next()) {
                    for (int i = 1; i < antColumns; i++) {
                        String data = rs.getString(i);
                        printWriter.print("\"" + (data == null ? "" : data) + "\",");
                    }
                    String data = rs.getString(antColumns);
                    printWriter.print("\"" + (data == null ? "" : data) + "\";");
                }
                printWriter.flush();
            });
            return null;
        });
    }
}
