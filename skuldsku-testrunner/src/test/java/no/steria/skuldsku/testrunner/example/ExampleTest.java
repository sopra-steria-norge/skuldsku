package no.steria.skuldsku.testrunner.example;

import com.jolbox.bonecp.BoneCPDataSource;
import no.steria.skuldsku.recorder.dbrecorder.DatabaseRecorder;
import no.steria.skuldsku.recorder.dbrecorder.impl.oracle.OracleDatabaseRecorder;
import no.steria.skuldsku.testrunner.dbrunner.testrunner.DatabaseRecorderCallback;
import no.steria.skuldsku.testrunner.dbrunner.testrunner.DatabaseRecorderControl;
import no.steria.skuldsku.testrunner.dbrunner.testrunner.DatabaseRecorderRunner;
import no.steria.skuldsku.testrunner.dbrunner.testrunner.DatabaseRecorderRunnerConfig;
import no.steria.skuldsku.utils.Jdbc;
import no.steria.skuldsku.utils.SimpleTransactionManager;
import no.steria.skuldsku.utils.TransactionCallback;
import no.steria.skuldsku.utils.TransactionManager;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.PrintWriter;

public class ExampleTest {
    
    private static DatabaseRecorderRunner databaseRecorder;
    
    @BeforeClass
    public static void setup() {
        final DatabaseRecorderRunnerConfig config = new DatabaseRecorderRunnerConfig();
        //config.setRollbackEnabled(false);
        databaseRecorder = new DatabaseRecorderRunner(createDataSource("jdbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb", "wimpel_dba", "wimpel"), config);
    }
    
    @Test
    @Ignore ("This test fails when the session id changes")
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
        //databaseRecorder.initialize();
        //databaseRecorder.start();
        final PrintWriter out = new PrintWriter(System.out);
        databaseRecorder.exportTo(out);
        out.flush();
        System.out.println("OK");
    }
}
