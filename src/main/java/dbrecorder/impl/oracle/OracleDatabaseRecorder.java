package dbrecorder.impl.oracle;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import utils.Jdbc;
import utils.JdbcException;
import utils.ResultSetCallback;
import utils.SimpleTransactionManager;
import utils.SqlUtils;
import utils.TransactionCallback;
import utils.TransactionManager;
import dbrecorder.DatabaseRecorder;

public class OracleDatabaseRecorder implements DatabaseRecorder {

    private static final int MAX_TRIGGER_NAME_LENGTH = 30;
    private static final String RECORDER_PREFIX = "DBR_";

    
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
                jdbc.query("SELECT 'CLIENT_IDENTIFIER='||CLIENT_IDENTIFIER||';SESSION_USER='||SESSION_USER||';SESSIONID='||SESSIONID||';TABLE_NAME='||TABLE_NAME||';ACTION='||ACTION||';'||DATAROW AS DATA FROM DBR_RECORDER", new ResultSetCallback() {
                    @Override
                    public void extractData(ResultSet rs) throws SQLException {
                        out.println(rs.getString(1));
                    }
                });
                return null;
            }
        });
    }
    
    public void exportAndRemove(final PrintWriter out) {
        final List<String> retrivedDataIds = new ArrayList<String>();
        transactionManager.doInTransaction(new TransactionCallback<Object>() {
            @Override
            public Object callback(Jdbc jdbc) {
                jdbc.query("SELECT DBR_ID, 'CLIENT_IDENTIFIER='||CLIENT_IDENTIFIER||';SESSION_USER='||SESSION_USER||';SESSIONID='||SESSIONID||';TABLE_NAME='||TABLE_NAME||';ACTION='||ACTION||';'||DATAROW AS DATA FROM DBR_RECORDER", new ResultSetCallback() {
                    @Override
                    public void extractData(ResultSet rs) throws SQLException {
                        retrivedDataIds.add(rs.getString(1));
                        out.println(rs.getString(2));
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
                for (String id : retrivedDataIds) {
                    jdbc.execute("DELETE FROM DBR_RECORDER WHERE DBR_ID = " + id);
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
                    jdbc.execute("DROP TABLE DBR_RECORDER");
                } catch (JdbcException e) {}
                try {
                    jdbc.execute("DROP SEQUENCE DBR_RECORDER_ID_SEQ");
                } catch (JdbcException e) {}
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
                jdbc.execute(SqlUtils.readSql("create_recorder_table.sql"));
                jdbc.execute("CREATE SEQUENCE DBR_RECORDER_ID_SEQ");
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
                        return null;
                    }
                    createTrigger(jdbc, tableName, columnNames);
                }
                return null;
            }
        });
    }

    private void createTrigger(Jdbc jdbc, String tableName, final List<String> columnNames) {
        final String triggerSql = TriggerSqlGenerator.generateTriggerSql(
                reduceToMaxLength(RECORDER_PREFIX + tableName, MAX_TRIGGER_NAME_LENGTH),
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
        return resourceName.startsWith(RECORDER_PREFIX);
    }
    
    String reduceToMaxLength(String s, int length) {
        return (s.length() <= length) ? s : s.substring(0, length);
    }
}
