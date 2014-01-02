package dbrecorder;

import java.io.PrintWriter;

import javax.sql.DataSource;

import com.jolbox.bonecp.BoneCPDataSource;

import dbrecorder.impl.oracle.OracleDatabaseRecorder;


public final class DatabaseRecorderMain {
    /*
     * exec dbms_session.set_identifier(USER||'foobar');
     * SELECT sys_context('USERENV', 'CLIENT_IDENTIFIER') FROM DUAL;
     */
    
    private DatabaseRecorderMain() {}
    
    
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

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Usage: dbrecorder JDBC_URL USERNAME PASSWORD setup|start|stop|export FILE|tearDown");
            System.exit(1);
        }
        
        final String jdbcUrl = args[0];
        final String username = args[1];
        final String password = args[2];
        
        final DataSource dataSource = createDataSource(jdbcUrl, username, password);
        final DatabaseRecorder databaseRecorder = new OracleDatabaseRecorder(dataSource);
        
        if (args[3].equals("setup")) {
            databaseRecorder.setup();
            System.out.println("Database recorder initialized but not started. Invoke \"start\" to begin recording.");
        } else if (args[3].equals("start")) {
            databaseRecorder.start();
            System.out.println("Database recording started.");
        } else if (args[3].equals("stop")) {
            databaseRecorder.stop();
            System.out.println("Database recording stopped.");
        } else if (args[3].equals("exportTo")) {
            final String exportFile = args[4];
            final PrintWriter out = new PrintWriter(exportFile);
            try {
                databaseRecorder.exportTo(out);
            } finally {
                out.close();
            }
            System.out.println("Stopped recording and exported data to: " + exportFile);
        } else if (args[3].equals("tearDown")) {
            databaseRecorder.tearDown();
            System.out.println("Database recording stopped and data cleared.");
        }
    }
}
