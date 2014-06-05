package no.steria.copito.testrunner;

import java.io.File;
import java.io.PrintWriter;

import javax.sql.DataSource;

import com.jolbox.bonecp.BoneCPDataSource;

import no.steria.copito.dbrecorder.dbrecorder.DatabaseRecorder;
import no.steria.copito.dbrecorder.dbrecorder.impl.oracle.OracleDatabaseRecorder;
import no.steria.copito.testrunner.dbrunner.dbchange.DatabaseChangeRollback;

/**
 * Support for running the <code>DatbaseRecorder</code> from the command-line.
 * 
 * @see no.steria.copito.dbrecorder.dbrecorder.DatabaseRecorder
 */
public final class DatabaseRecorderMain {
    
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

    static void printUsage() {
        System.out.println("Usage: dbrecorder JDBC_URL USERNAME PASSWORD setup|start|stop|export FILE|tearDown|rollback FILE");
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            printUsage();
            System.exit(1);
        }
        
        final String jdbcUrl = args[0];
        final String username = args[1];
        final String password = args[2];
        
        final DataSource dataSource = createDataSource(jdbcUrl, username, password);       
        final DatabaseRecorder databaseRecorder = new OracleDatabaseRecorder(dataSource);
        final DatabaseChangeRollback rollback = new DatabaseChangeRollback(dataSource);
        
        int i = 3;
        
        while (i < args.length) {
            if (args[i].equals("setup")) {
                databaseRecorder.setup();
                System.out.println("Database recorder initialized but not started. Invoke \"start\" to begin recording.");
            } else if (args[i].equals("start")) {
                databaseRecorder.start();
                System.out.println("Database recording started.");
            } else if (args[i].equals("stop")) {
                databaseRecorder.stop();
                System.out.println("Database recording stopped.");
            } else if (args[i].equals("exportTo")) {
                i++;
                final String exportFile = args[i];
                final PrintWriter out = new PrintWriter(exportFile);
                try {
                    databaseRecorder.exportTo(out);
                } finally {
                    out.close();
                }
                System.out.println("Data exported to: " + exportFile);
            } else if (args[i].equals("tearDown")) {
                databaseRecorder.tearDown();
                System.out.println("Database recording stopped and data cleared.");
            } else if (args[i].equals("rollback")) {
                i++;
                final File rollbackFile = new File(args[i]);
                rollback.rollback(rollbackFile);
                System.out.println("Database changes rolled back: " + args[i]);
            } else {
                printUsage();
                System.err.println("Unknown parameter: " + args[i]);
                System.exit(1);
            }
            i++;
        }
    }
}
