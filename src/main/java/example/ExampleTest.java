package example;

import java.io.PrintWriter;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import runner.DatabaseRecorderCallback;
import runner.DatabaseRecorderControl;
import runner.DatabaseRecorderRunner;
import runner.DatabaseRecorderRunnerConfig;
import utils.Jdbc;
import utils.SimpleTransactionManager;
import utils.TransactionCallback;
import utils.TransactionManager;

import com.jolbox.bonecp.BoneCPDataSource;

import dbrecorder.DatabaseRecorder;
import dbrecorder.impl.oracle.OracleDatabaseRecorder;

public class ExampleTest {
    
    private static DatabaseRecorderRunner databaseRecorder;
    
    @BeforeClass
    public static void setup() {
        final DatabaseRecorderRunnerConfig config = new DatabaseRecorderRunnerConfig();
        //config.setRollbackEnabled(false);
        databaseRecorder = new DatabaseRecorderRunner(createDataSource("jdbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb", "wimpel_dba", "wimpel"), config);
    }
    
    @Test
    public void exampleTest() {
        databaseRecorder.recordAndCompare(new DatabaseRecorderCallback() {
            @Override
            public void execute(DatabaseRecorderControl control) {
                
                // Generate actual output by doing something in the database:
                final TransactionManager tm = new SimpleTransactionManager(createDataSource("jdbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb", "wimpel_dba", "wimpel"));
                tm.doInTransaction(new TransactionCallback<Object>() {
                    @Override
                    public Object callback(Jdbc jdbc) {
                        jdbc.execute("Insert into T_HENDELSE (HEND_ID,SOEK_SAKSNR_WIMPEL,HEND_TYPE_ID,STAT_KODE,HEND_DATO,HEND_AKTOER,HEND_AKTOER_NR,HEND_DOKUMENT_NR) values (66666666,47778,1,'UU',to_date('10-SEP-13','DD-MON-RR'),'Test Testesen','01106000057',2013017156)");
                        return null;
                    }
                });
                
            }
        }, "exampleTest.txt");
    }
    
    static DataSource createDataSource(String jdbcUrl, String username, String password) {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        
        final BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
    
    @Test
    @Ignore
    public void control() {
        final DatabaseRecorder databaseRecorder = new OracleDatabaseRecorder(createDataSource("jdbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb", "wimpel_dba", "wimpel"));
        //databaseRecorder.tearDown();
        //databaseRecorder.setup();
        //databaseRecorder.start();
        final PrintWriter out = new PrintWriter(System.out);
        databaseRecorder.exportTo(out);
        out.flush();
        System.out.println("OK");
    }
}
