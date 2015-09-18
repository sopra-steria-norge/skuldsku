package no.steria.skuldsku.recorder.recorders;

import no.steria.skuldsku.DatabaseTableNames;
import no.steria.skuldsku.recorder.logging.RecorderLog;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static no.steria.skuldsku.DatabaseTableNames.SKULDSKU_DATABASE_TABLE_PREFIX;

public class DatabaseRecorderCommunicator extends AbstractRecorderCommunicator {
    private final DataSource dataSource;
    private static final String TABLENAME = DatabaseTableNames.SKULDSKU_DATABASE_TABLE_PREFIX + "RECORD";
    private static final String SEQUENCENAME = DatabaseTableNames.SKULDSKU_DATABASE_TABLE_PREFIX + "RECORD_ID_SEQ";

    public DatabaseRecorderCommunicator(DataSource dataSource) {
        this.dataSource = dataSource;
        try (Connection conn = dataSource.getConnection()) {
            RecorderLog.debug("Got connection.");
            if (!tableExists(conn)) {
                createTable(conn);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void exportTo(final PrintWriter out, DataSource dataSource) throws SQLException {
        String sql = "SELECT DATA FROM " + TABLENAME + " ORDER BY " + SKULDSKU_DATABASE_TABLE_PREFIX + "ID";
        try(PreparedStatement statement = dataSource.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                out.println(resultSet.getString(1));
            }
            out.flush();
        }
    }

    private void createTable(Connection conn) throws SQLException {
        System.out.println("Creating table");
        String sql = "CREATE TABLE " + TABLENAME + "( " +
                SKULDSKU_DATABASE_TABLE_PREFIX + "ID NUMBER, " +
                "DATA CLOB null,  " +
                "CREATED TIMESTAMP (6) DEFAULT sysdate)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.execute();
        }
        try (PreparedStatement statement = conn.prepareStatement("CREATE SEQUENCE " + SEQUENCENAME)) {
            statement.execute();
        }
    }

    private boolean tableExists(Connection conn) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("select table_name from all_tables where table_name='" + TABLENAME + "'")) {
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }


    @Override
    protected void saveRecord(String res) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("insert into " + TABLENAME + "(" + SKULDSKU_DATABASE_TABLE_PREFIX + "ID" + ", data) values (" + SEQUENCENAME + ".nextval, ?)")) {
                statement.setString(1, res);
                statement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
