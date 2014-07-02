package no.steria.copito.recorder.dbrecorder.impl.oracle;

import no.steria.copito.recorder.dbrecorder.DatabaseRecorder;
import no.steria.copito.utils.*;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static no.steria.copito.recorder.Recorder.COPITO_DATABASE_TABLE_PREFIX;

public class OracleDatabaseRecorder implements DatabaseRecorder {

    private static final int MAX_TRIGGER_NAME_LENGTH = 30;

    
    private final TransactionManager transactionManager;
    
    
    public OracleDatabaseRecorder(DataSource dataSource) {
            this(new SimpleTransactionManager(dataSource));
    }
    
    OracleDatabaseRecorder(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    
    public void setup() {
        createRecorderTable();
    }
    
    public void start() {
        createTriggers();
    }
    
    public void stop() {
        dropRecorderTriggers();
    }
    
    public void tearDown() {
        dropRecorderTriggers();
        dropRecorderTable();
    }

    public void exportTo(final PrintWriter out) {
        transactionManager.doInTransaction(new TransactionCallback<Object>() {
            @Override
            public Object callback(Jdbc jdbc) {
                jdbc.query("SELECT 'CLIENT_IDENTIFIER='||CLIENT_IDENTIFIER||';SESSION_USER='||SESSION_USER||';SESSIONID='||SESSIONID||';TABLE_NAME='||TABLE_NAME||';ACTION='||ACTION||';'||DATAROW AS DATA FROM " + COPITO_DATABASE_TABLE_PREFIX + "RECORDER", new ResultSetCallback() {
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
                jdbc.query("SELECT " + COPITO_DATABASE_TABLE_PREFIX + "ID, 'CLIENT_IDENTIFIER='||CLIENT_IDENTIFIER||';SESSION_USER='||SESSION_USER||';SESSIONID='||SESSIONID||';TABLE_NAME='||TABLE_NAME||';ACTION='||ACTION||';'||DATAROW AS DATA FROM " + COPITO_DATABASE_TABLE_PREFIX + "RECORDER", new ResultSetCallback() {
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
                    jdbc.execute("DELETE FROM " + COPITO_DATABASE_TABLE_PREFIX + "RECORDER WHERE " + COPITO_DATABASE_TABLE_PREFIX + "ID = " + id);
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
                    jdbc.execute("DROP TABLE " + COPITO_DATABASE_TABLE_PREFIX + "RECORDER");
                } catch (JdbcException e) {
                    e.printStackTrace();
                }
                try {
                    jdbc.execute("DROP SEQUENCE " + COPITO_DATABASE_TABLE_PREFIX + "RECORDER_ID_SEQ");
                } catch (JdbcException e) {
                    e.printStackTrace();
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
                    if (isRecorderResource(triggerName)) {
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
    
    void createRecorderTable() {
        transactionManager.doInTransaction(new TransactionCallback<Object>() {
            @Override
            public Object callback(Jdbc jdbc) {
                jdbc.execute(SqlUtils.readSql("dbrecorder/create_recorder_table.sql"));
                jdbc.execute("CREATE SEQUENCE " + COPITO_DATABASE_TABLE_PREFIX + "RECORDER_ID_SEQ");
                return null;
            }
        });
    }
    
    void createTriggers() {
        transactionManager.doInTransaction(new TransactionCallback<Object>() {
            @Override
            public Object callback(Jdbc jdbc) {
                for (String tableName : getTableNames(jdbc)) {
                    if (isRecorderResource(tableName)) {
                        continue;
                    }
                    final List<String> columnNames = getColumnNames(jdbc, tableName);
                    if (columnNames.isEmpty()) {
                        // TODO: Log ignoring
                        System.out.println("Ignoring table with no columns: " + tableName);
                        continue;
                    }
                    try {
                        createTrigger(jdbc, tableName, columnNames);
                    } catch (JdbcException e) {
                        // TODO: Log exception
                        System.out.println("Ignoring table: " + tableName + " (" + e.getMessage() + ")");
                    }
                }
                return null;
            }
        });
    }

    private void createTrigger(Jdbc jdbc, String tableName, final List<String> columnNames) {
        final String triggerSql = TriggerSqlGenerator.generateTriggerSql(
                reduceToMaxLength(COPITO_DATABASE_TABLE_PREFIX + tableName, MAX_TRIGGER_NAME_LENGTH),
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

    private boolean isRecorderResource(String resourceName) {
        return resourceName.startsWith(COPITO_DATABASE_TABLE_PREFIX);
    }
    
    String reduceToMaxLength(String s, int length) {
        return (s.length() <= length) ? s : s.substring(0, length);
    }
}
