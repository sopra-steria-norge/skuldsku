/**
 * A command line interface for running the database recorder, and for rolling the database back.
 */
package no.steria.copito.testrunner;

import com.jolbox.bonecp.BoneCPDataSource;
import no.steria.copito.recorder.logging.RecorderLog;
import no.steria.copito.testrunner.dbrunner.dbchange.DatabaseChangeRollback;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Support for running the <code>DatbaseRecorder</code> from the command-line.
 *
 * @see no.steria.copito.recorder.dbrecorder.DatabaseRecorder
 */
public class TestRunnerCmd {

    private static final String DATABASE_DRIVER = "oracle.jdbc.OracleDriver";
    private static String databaseRecordings;
    private static String javaInterfaceRecordings;
    private static String httpRecordings;
    private static BoneCPDataSource dataSource;
    private static SQLExec sqlExec = new SQLExec();

    public static void main(String[] args) throws IOException, SQLException {

        Scanner sc = new Scanner(System.in);
        args = prepareDataSource(args, sc);

        int currentIndex;
        if(args.length > 6){
            currentIndex = 6;
        } else {
            currentIndex = 0;
            printUsage();
            args = getNewArgumentsFromUser(sc);
        }

        while(args.length != 0) {
            switchCommand(args, currentIndex);
            currentIndex = 0;
            printUsage();
            args = getNewArgumentsFromUser(sc);
        }
    }

    private static String[] prepareDataSource(String[] args, Scanner sc) throws SQLException {
        args = readNecessaryParameters(args, sc);
        ensureDbDriverIsAvailable();
        initializeDataSource(args);
        assignTableNames(args);
        System.out.println("Connection details are registered.");
        return args;
    }

    private static void assignTableNames(String[] args) {
        databaseRecordings = args[3];
        javaInterfaceRecordings = args[4];
        httpRecordings = args[5];
    }

    private static void initializeDataSource(String[] args) {
        dataSource = new BoneCPDataSource();
        dataSource.setJdbcUrl(args[0]);
        dataSource.setUsername(args[1]);
        dataSource.setPassword(args[2]);
    }

    private static void ensureDbDriverIsAvailable() {
        try {
            Class.forName(DATABASE_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String[] readNecessaryParameters(String[] args, Scanner sc) {
        String command;
        while (args.length < 6) {
            System.out.println("You must first enter connection details. Please provide the following parameters in the" +
                    " order specified:\n<jdbc url> <username> <password> <table name database recordings> <table name " +
                    "Java interface recordings> <table name HTTP recordings>");

            command = sc.nextLine();
            System.out.println(command);
            args = command.split(" ");
        }
        return args;
    }

    private static void switchCommand(String[] args, int currentIndex) throws IOException, SQLException {

        while (currentIndex < args.length) {
            if (args[currentIndex].equals("export")) {
                if(args.length < 2) {
                    System.out.println("Cannot export without a file name!");
                } else {
                    currentIndex++;
                    exportToFile(args[currentIndex]);
                    System.out.println("Data exported to: " + args[currentIndex]);
                }
            } else if (args[currentIndex].equals("rollback")) {
                currentIndex = rollback(args[currentIndex], currentIndex);
            } else if(args[currentIndex].equals("clean")) {
                cleanRecordingTables(dataSource);
            } else if(args[currentIndex].equals("import")) {
                if(args.length < currentIndex + 1) {
                    System.out.println("Please provide file name for database script!");
                    printUsage();
                    continue;
                }
                importDbScript(args[currentIndex + 1]);
                System.out.println("done");
            }
            else {
                System.err.println("Unknown command: " + args[currentIndex]);
                printUsage();
            }
            currentIndex++;
        }
    }

    private static int rollback(String arg, int currentIndex) {
        currentIndex++;
        final File rollbackFile = new File(arg);
        DatabaseChangeRollback rollback = new DatabaseChangeRollback(dataSource);
        rollback.rollback(rollbackFile);
        System.out.println("Database changes rolled back: " + arg);
        return currentIndex;
    }

    private static void importDbScript(String file) {
        sqlExec.setTaskType("sql");
        sqlExec.setTaskName("sql");
        Project project = new Project();
        project.init();
        sqlExec.setProject(project);
        sqlExec.setSrc(new File(file));
        sqlExec.setDriver(DATABASE_DRIVER);
        sqlExec.setPassword(dataSource.getPassword());
        sqlExec.setUserid(dataSource.getUsername());
        sqlExec.setUrl(dataSource.getJdbcUrl());
        sqlExec.execute();

    }

    private static void exportToFile(String arg) {
        try (OutputStream os = new FileOutputStream(arg)) {
            DbToFileExporter.exportTo(os, databaseRecordings, javaInterfaceRecordings, httpRecordings, dataSource);
        } catch (IOException e) {
            RecorderLog.error("Could not write to specified file.", e);
        }
    }

    private static void cleanRecordingTables(DataSource dataSource) throws SQLException {
        PreparedStatement dbRecDelete = dataSource.getConnection().prepareStatement("DELETE FROM " + databaseRecordings);
        PreparedStatement javaIntRecDelete = dataSource.getConnection().prepareStatement("DELETE FROM " + javaInterfaceRecordings);
        PreparedStatement httpRecDelete = dataSource.getConnection().prepareStatement("DELETE FROM " + httpRecordings);
        dbRecDelete.execute();
        javaIntRecDelete.execute();
        httpRecDelete.execute();
    }

    private static String[] getNewArgumentsFromUser(Scanner sc) {
        return sc.nextLine().split(" ");
    }

    private static void printUsage() {
        System.out.println("Usage: setup|start|stop|export <file name> |tearDown|rollback FILE");
    }

    // method for mocking out SQLExec when testing
    static void setSqlExec(SQLExec sqlExec) {
        TestRunnerCmd.sqlExec = sqlExec;
    }

    // "main" method for testing. Mocks out the data source.
    static void testMain(String[] args, BoneCPDataSource dataSource) throws IOException, SQLException {
        Scanner sc = new Scanner(System.in);
        prepareDataSource(args, sc);
        TestRunnerCmd.dataSource = dataSource;
        switchCommand(args, 6);
    }
}
