package no.steria.skuldsku.testrunner;

import au.com.bytecode.opencsv.CSVWriter;
import no.steria.skuldsku.recorder.logging.RecorderLog;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static no.steria.skuldsku.DatabaseTableNames.*;

public class DbToFileExporter {

    public static final String DATABASE_RECORDINGS_HEADER = " **DATABASE RECORDINGS** ";
    public static final String JAVA_INTERFACE_RECORDINGS_HEADER = " **JAVA INTERFACE RECORDINGS** ";
    public static final String HTTP_RECORDINGS_HEADER = " **HTTP RECORDINGS** ";

    public static final int ANT_COLUMNS_DATABASE_RECORDINGS = 7;
    public static final int ANT_COLUMNS_JAVA_INTERFACE_RECORDINGS = 7;
    public static final int ANT_COLUMNS_HTTP_RECORDINGS = 6;

    /**
     * After the recording of data, this method can be used to extract the data and write it to file,
     * so that it can be used for running tests.
     *
     * @param dataSource The data source for the database that contains the tables
     * @param os         The stream to write to.
     */
    public static void exportTo(OutputStream os, DataSource dataSource) throws SQLException {

        try (PrintWriter printWriter = new PrintWriter(os)) {

            CSVWriter csvWriter = new CSVWriter(printWriter, ',', '"');
            printWriter.write("\n" + DATABASE_RECORDINGS_HEADER + "\n\n");
            exportRecordingsToFile(csvWriter, DATABASE_RECORDINGS_TABLE, ANT_COLUMNS_DATABASE_RECORDINGS, dataSource);
            printWriter.write("\n" + JAVA_INTERFACE_RECORDINGS_HEADER + "\n\n");
            exportRecordingsToFile(csvWriter, JAVA_INTERFACE_RECORDINGS_TABLE, ANT_COLUMNS_JAVA_INTERFACE_RECORDINGS, dataSource);
            printWriter.write("\n" + HTTP_RECORDINGS_HEADER + "\n\n");
            exportRecordingsToFile(csvWriter, HTTP_RECORDINGS_TABLE + " ORDER BY TIMEST", ANT_COLUMNS_HTTP_RECORDINGS, dataSource);
            printWriter.flush();
            os.flush();
        } catch (IOException e) {
            RecorderLog.error("Could not export data to file.", e);
        }
    }

    private static void exportRecordingsToFile(final CSVWriter csvWriter, String table, int antColumns, DataSource dataSource) throws SQLException, IOException {
        PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement("SELECT * FROM " + table);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String[] entries = new String[antColumns];
            for (int i = 1; i <= antColumns; i++) {
                entries[i - 1] = resultSet.getString(i);
            }
            csvWriter.writeNext(entries);
        }
        csvWriter.flush();
    }
}
