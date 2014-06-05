package no.steria.copito.testrunner.example;

import java.io.PrintWriter;

import javax.sql.DataSource;

import no.steria.copito.testrunner.dbrunner.testrunner.DatabaseRecorderCallback;
import no.steria.copito.testrunner.dbrunner.testrunner.DatabaseRecorderControl;
import no.steria.copito.testrunner.dbrunner.testrunner.DatabaseRecorderRunner;
import no.steria.copito.testrunner.dbrunner.testrunner.DatabaseRecorderRunnerConfig;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import no.steria.copito.utils.Jdbc;
import no.steria.copito.utils.SimpleTransactionManager;
import no.steria.copito.utils.TransactionCallback;
import no.steria.copito.utils.TransactionManager;

import com.jolbox.bonecp.BoneCPDataSource;

import no.steria.copito.dbrecorder.DatabaseRecorder;
import no.steria.copito.dbrecorder.impl.oracle.OracleDatabaseRecorder;

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
        //databaseRecorder.setup();
        //databaseRecorder.start();
        final PrintWriter out = new PrintWriter(System.out);
        databaseRecorder.exportTo(out);
        out.flush();
        System.out.println("OK");
    }
}
