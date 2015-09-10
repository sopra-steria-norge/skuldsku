package no.steria.skuldsku.recorder.dbrecorder.impl.oracle;

import no.steria.skuldsku.recorder.dbrecorder.DatabaseRecorder;
import no.steria.skuldsku.recorder.logging.RecorderLog;
import no.steria.skuldsku.utils.*;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static no.steria.skuldsku.DatabaseTableNames.SKULDSKU_DATABASE_TABLE_PREFIX;
import static no.steria.skuldsku.DatabaseTableNames.DATABASE_RECORDINGS_TABLE;

public class OracleDatabaseRecorder implements DatabaseRecorder {

    private static final int MAX_TRIGGER_NAME_LENGTH = 30;

    private List<String> ignoredTables = new ArrayList<>();

    private final TransactionManager transactionManager;

    
    public OracleDatabaseRecorder(DataSource dataSource) {
        this(new SimpleTransactionManager(dataSource), new ArrayList<String>(0));
    }

    public OracleDatabaseRecorder(DataSource dataSource, List<String> ignoredTables) {
        this(new SimpleTransactionManager(dataSource), ignoredTables);
    }

    OracleDatabaseRecorder(TransactionManager transactionManager, List<String> ignoredTables) {
        this.transactionManager = transactionManager;
        this.ignoredTables = ignoredTables;
    }

    @Override
    public void initialize() {
        transactionManager.doInTransaction(new TransactionCallback<Object>() {
            @Override
            public Object callback(Jdbc jdbc) {
                createRecorderTableIfNotExists(jdbc);
                createDbSequenceIfNotExists(jdbc);
                return null;
            }
        });
    }

    @Override
    public void start() {
        createTriggersInTransaction();
    }

    @Override
    public void stop() {
        dropRecorderTriggers();
    }

    @Override
    public void tearDown() {
        dropRecorderTriggers();
        dropRecorderTable();
    }

    @Override
    public void exportTo(final PrintWriter out) {
        transactionManager.doInTransaction(new TransactionCallback<Object>() {
            @Override
            public Object callback(Jdbc jdbc) {
                jdbc.query("SELECT 'CLIENT_IDENTIFIER='||CLIENT_IDENTIFIER||';SESSION_USER='||SESSION_USER||';SESSIONID='|" +
                        "|SESSIONID||';TABLE_NAME='||TABLE_NAME||';ACTION='||ACTION||';'||DATAROW AS DATA FROM " +
                        DATABASE_RECORDINGS_TABLE + "ORDER BY SKS_ID", new ResultSetCallback() {
                    @Override
                    public void extractData(ResultSet rs) throws SQLException {
                        while (rs.next()) {
                            out.println(rs.getString(1));
                        }
                    }
                });
                return null;
            }
        });
    }

    /**
     *
     * @param out Exports all database recordings, but without the field session ID, so that results from different
     *            executions can be easily compared (for unit-test level integration tests).
     */
    public void exportWithoutSessionIdTo(final PrintWriter out) {
        transactionManager.doInTransaction(new TransactionCallback<Object>() {
            @Override
            public Object callback(Jdbc jdbc) {
                jdbc.query("SELECT 'CLIENT_IDENTIFIER='||CLIENT_IDENTIFIER||';SESSION_USER='||SESSION_USER||'" +
                        ";TABLE_NAME='||TABLE_NAME||';ACTION='||ACTION||';'||DATAROW AS DATA FROM " +
                        DATABASE_RECORDINGS_TABLE + "ORDER BY SKS_ID", new ResultSetCallback() {
                    @Override
                    public void extractData(ResultSet rs) throws SQLException {
                        while (rs.next()) {
                            out.println(rs.getString(1));
                        }
                    }
                });
                return null;
            }
        });
    }

    public void exportAndRemove(final PrintWriter out) {
        final List<String> retrievedDataIds = new ArrayList<>();
        transactionManager.doInTransaction(new TransactionCallback<Object>() {
            @Override
            public Object callback(Jdbc jdbc) {
                jdbc.query("SELECT " + SKULDSKU_DATABASE_TABLE_PREFIX + "ID, 'CLIENT_IDENTIFIER='||CLIENT_IDENTIFIER||';SESSION_USER='||SESSION_USER||';SESSIONID='||SESSIONID||';TABLE_NAME='||TABLE_NAME||';ACTION='||ACTION||';'||DATAROW AS DATA FROM " + DATABASE_RECORDINGS_TABLE + "ORDER BY SKS_ID", new ResultSetCallback() {
                    @Override
                    public void extractData(ResultSet rs) throws SQLException {
                        while (rs.next()) {
                            retrievedDataIds.add(rs.getString(1));
                            out.println(rs.getString(2));
                        }
                    }
                });
                return null;
            }
        });
        out.flush();

        /*
         * Deleting after select in order to avoid forcing the database to maintain
         * two different versions of the data while the select is running. 
         */
        transactionManager.doInTransaction(new TransactionCallback<Object>() {
            @Override
            public Object callback(Jdbc jdbc) {
                for (String id : retrievedDataIds) {
                    jdbc.execute("DELETE FROM " + DATABASE_RECORDINGS_TABLE + " WHERE " + SKULDSKU_DATABASE_TABLE_PREFIX + "ID = " + id);
                }
                return null;
            }
        });
    }


    void dropRecorderTable() {
        transactionManager.doInTransaction(new TransactionCallback<Object>() {
            @Override
            public Object callback(Jdbc jdbc) {
                try {
                    jdbc.execute("DROP TABLE " + DATABASE_RECORDINGS_TABLE);
                } catch (JdbcException e) {
                    RecorderLog.error("Could not drop table " + DATABASE_RECORDINGS_TABLE, e);
                }
                try {
                    jdbc.execute("DROP SEQUENCE " + SKULDSKU_DATABASE_TABLE_PREFIX + "RECORDER_ID_SEQ");
                } catch (JdbcException e) {
                    RecorderLog.error("Could not drop sequence " + SKULDSKU_DATABASE_TABLE_PREFIX + "RECORDER_ID_SEQ", e);
                }
                return null;
            }
        });
    }

    private void dropRecorderTriggers() {
        transactionManager.doInTransaction(new TransactionCallback<Object>() {
            @Override
            public Object callback(Jdbc jdbc) {
                for (String triggerName : getTriggerNames(jdbc)) {
                    if (isSkuldskuTrigger(triggerName)) {
                        jdbc.execute("DROP TRIGGER " + triggerName);
                    }
                }
                return null;
            }
        });
    }

    List<String> getTriggerNames(Jdbc jdbc) {
        return jdbc.queryForList("SELECT TRIGGER_NAME FROM USER_TRIGGERS", String.class);
    }

    private void createRecorderTableIfNotExists(Jdbc jdbc) {
        List<String> recorderTable = jdbc.queryForList(
                "select table_name from all_tables where table_name='" + DATABASE_RECORDINGS_TABLE + "'", String.class);
        if (recorderTable.isEmpty()) {
            jdbc.execute("CREATE TABLE " + DATABASE_RECORDINGS_TABLE + " (\n" +
                    "    " + SKULDSKU_DATABASE_TABLE_PREFIX + "ID             NUMBER,\n" +
                    "    CLIENT_IDENTIFIER  VARCHAR2(256),\n" +
                    "    SESSION_USER       VARCHAR2(256),\n" +
                    "    SESSIONID          VARCHAR2(256),\n" +
                    "    TABLE_NAME         VARCHAR2(30),\n" +
                    "    ACTION             VARCHAR2(6),\n" +
                    "    DATAROW            CLOB    \n" +
                    ")");
        }
    }

    private void createDbSequenceIfNotExists(Jdbc jdbc) {
        List<String> recorderTrigger = jdbc.queryForList(
                "select sequence_name from all_sequences where sequence_name='" + SKULDSKU_DATABASE_TABLE_PREFIX + "RECORDER_ID_SEQ'", String.class);

        if (recorderTrigger.isEmpty()) {
            jdbc.execute("CREATE SEQUENCE " + SKULDSKU_DATABASE_TABLE_PREFIX + "RECORDER_ID_SEQ");
        }
    }

    void createTriggersInTransaction() {
        transactionManager.doInTransaction(new TransactionCallback<Object>() {
            @Override
            public Object callback(Jdbc jdbc) {
                createTriggers(jdbc);
                return null;
            }
        });
    }

    void createTriggers(Jdbc jdbc) {
        for (String tableName : getTableNames(jdbc)) {
            if (isSkuldskuTrigger(tableName) || isIgnoredTable(tableName)) {
                continue;
            }
            final List<String> columnNames = getColumnNames(jdbc, tableName);
            if (columnNames.isEmpty()) {
                RecorderLog.debug("Ignoring table with no columns: " + tableName);
                continue;
            }
            try {
                createTrigger(jdbc, tableName, columnNames);
            } catch (JdbcException e) {
                RecorderLog.debug("Ignoring table: " + tableName + " (" + e.getMessage() + ")");
            }
        }
    }

    private void createTrigger(Jdbc jdbc, String tableName, final List<String> columnNames) {
        final String triggerSql = TriggerSqlGenerator.generateTriggerSql(
                reduceToMaxLength(SKULDSKU_DATABASE_TABLE_PREFIX + tableName, MAX_TRIGGER_NAME_LENGTH),
                tableName,
                columnNames);

        jdbc.execute(triggerSql);
    }

    List<String> getTableNames(Jdbc jdbc) {
        return jdbc.queryForList("SELECT TABLE_NAME FROM USER_TABLES", String.class);
    }

    List<String> getColumnNames(Jdbc jdbc, String tableName) {
        return jdbc.queryForList("SELECT COLUMN_NAME FROM USER_TAB_COLS WHERE TABLE_NAME = ? ORDER BY COLUMN_NAME",
                String.class, tableName);
    }

    private boolean isSkuldskuTrigger(String resourceName) {
        return resourceName.toUpperCase().startsWith(SKULDSKU_DATABASE_TABLE_PREFIX);
    }

    private boolean isIgnoredTable(String tableName) {
        for (String ignoredTable : ignoredTables) {
            if (tableName.equalsIgnoreCase(ignoredTable)) {
                return true;
            }
        }
        return false;
    }

    String reduceToMaxLength(String s, int length) {
        return (s.length() <= length) ? s : s.substring(0, length);
    }
}
