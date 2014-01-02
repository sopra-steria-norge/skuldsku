package dbrecorder;

import javax.sql.DataSource;

import org.junit.Test;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

import dbrecorder.impl.oracle.OracleDatabaseRecorder;

public class DatabaseRecorderTest {

    @Test
    public void setup() {
        //final Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");
        final BoneCPConfig config = new BoneCPConfig();
        config.setJdbcUrl("jdbc:hsqldb:mem:testdb");
        config.setUsername("SA");
        config.setPassword("");
        final DataSource dataSource = new BoneCPDataSource(config);
        
        DatabaseRecorder recorder = new OracleDatabaseRecorder(dataSource);
        recorder.setup();
        recorder.tearDown();
    }
}
