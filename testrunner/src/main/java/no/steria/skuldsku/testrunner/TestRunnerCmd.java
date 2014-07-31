/**
 * A command line interface for running the database recorder, and for rolling the database back.
 */
package no.steria.skuldsku.testrunner;

import com.jolbox.bonecp.BoneCPDataSource;
import no.steria.skuldsku.recorder.logging.RecorderLog;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChangeRollback;
import no.steria.skuldsku.testrunner.httprunner.HttpPlayer;
import no.steria.skuldsku.testrunner.httprunner.StreamDbPlayBack;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.steria.skuldsku.DatabaseTableNames.*;

/**
 * Support for running the <code>DatbaseRecorder</code> from the command-line.
 *
 * @see no.steria.skuldsku.recorder.dbrecorder.DatabaseRecorder
 */
public class TestRunnerCmd {

    private static final String DATABASE_DRIVER = "oracle.jdbc.OracleDriver";
    public static final int NUMBER_OF_ARGUMENTS_FOR_INITIALIZATION = 3;
    private static BoneCPDataSource dataSource;
    private static StreamDbPlayBack streamDbPlayBack = new StreamDbPlayBack();

    public static void main(String[] args) throws SQLException {
        Scanner sc;
        sc = new Scanner(System.in);
        args = prepareDataSource(args, sc);
        readAndExecuteCommands(args, sc);
    }

    private static void readAndExecuteCommands(String[] args, Scanner sc) {
        int currentIndex;
        if (argumentsAreExhausted(args)) {
            args = getNewArgumentsFromUser(sc);
            currentIndex = 0;
        } else {
            currentIndex = NUMBER_OF_ARGUMENTS_FOR_INITIALIZATION;
        }
        try {
            while (!args[currentIndex].equals("exit")) {
                if (args[currentIndex].equals("export")) {
                    currentIndex = commandExport(args, currentIndex);
                } else if (args[currentIndex].equals("rollback")) {
                    currentIndex = rollback(args[currentIndex], currentIndex);
                } else if (args[currentIndex].equals("clean")) {
                    cleanRecordingTables(dataSource);
                } else if (args[currentIndex].equals("import")) {
                    currentIndex = commandImport(args, currentIndex);
                } else if (args[currentIndex].equals("oracleImport")) {
                    currentIndex = commandOracleImport(args, currentIndex);
                } else if (args[currentIndex].equals("runtests") || args[currentIndex].equals("runtest")) {
                    currentIndex = commandRunTest(args, currentIndex);
                } else {
                    System.err.println("Unknown command: " + args[currentIndex]);
                }
                currentIndex++;
                if (args.length <= currentIndex) {
                    args = getNewArgumentsFromUser(sc);
                    currentIndex = 0;
                }
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static int commandRunTest(String[] args, int currentIndex) throws IOException, ClassNotFoundException {
        if (args.length < currentIndex + 3) {
            System.out.println("Please provide the name of the file with the recordings to be played, and the URL to play against!");
            return currentIndex;
        }
        runTest(args[++currentIndex], args[++currentIndex]);
        System.out.println("Done running tests.");
        return currentIndex;
    }

    private static int commandOracleImport(String[] args, int currentIndex) throws SQLException {
        if (args.length < currentIndex + 2) {
            System.out.println("Please provide file name for database script!");
            return currentIndex;
        }
        importOracleDbScript(args[++currentIndex]);
        System.out.println("Done importing database script.");
        return currentIndex;
    }

    private static int commandExport(String[] args, int currentIndex) {
        if (args.length < currentIndex + 2) {
            System.out.println("Cannot export without a file name!");
            return currentIndex;
        }
        exportToFile(args[++currentIndex]);
        System.out.println("Data exported to: " + args[currentIndex]);
        return currentIndex;
    }

    private static int commandImport(String[] args, int currentIndex) throws SQLException, IOException {
        if (args.length < currentIndex + 2) {
            System.out.println("Please provide file name for database script!");
            return currentIndex;
        }
        importDbScript(args[++currentIndex]);
        System.out.println("Done importing database script.");
        return currentIndex;
    }


    private static boolean argumentsAreExhausted(String[] args) {
        return args.length <= NUMBER_OF_ARGUMENTS_FOR_INITIALIZATION;
    }

    private static String[] prepareDataSource(String[] args, Scanner sc) throws SQLException {
        args = readNecessaryParameters(args, sc);
        ensureDbDriverIsAvailable();
        initializeDataSource(args);
        System.out.println("Connection details are registered.");
        return args;
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

    private static void runTest(String recordingsPath, String url) throws IOException, ClassNotFoundException {
        FileInputStream recordings = new FileInputStream(recordingsPath);
        HttpPlayer httpPlayer = new HttpPlayer(url);
        streamDbPlayBack.play(recordings, httpPlayer);
        recordings.close();
    }

    private static String[] readNecessaryParameters(String[] args, Scanner sc) {
        String command;
        while (args.length < NUMBER_OF_ARGUMENTS_FOR_INITIALIZATION) {
            System.out.println("You must first enter connection details. Please provide the following parameters in the" +
                    " order specified:\n<jdbc url> <username> <password>");
            command = sc.nextLine();
            System.out.println(command);
            args = command.split(" ");
        }
        return args;
    }

    private static int rollback(String arg, int currentIndex) {
        currentIndex++;
        final File rollbackFile = new File(arg);
        DatabaseChangeRollback rollback = new DatabaseChangeRollback(dataSource);
        rollback.rollback(rollbackFile);
        System.out.println("Database changes rolled back: " + arg);
        return currentIndex;
    }

    private static void importDbScript(String file) throws SQLException, IOException {
        Connection connection = dataSource.getConnection();
        String currentSql = "";
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
        String available;
        while ((available = br.readLine()) != null) {
            String[] queryParts = available.split(";");
            queryParts[0] = currentSql + "\n" + queryParts[0]; //the first token might be the end of previous command
            for (int i = 1; queryParts.length > i; i++) {
                executeSql(connection, queryParts[i - 1]);
            }
            if (available.endsWith(";")) {
                executeSql(connection, queryParts[queryParts.length - 1]);
                currentSql = "";
            } else {
                currentSql = queryParts[queryParts.length - 1];
            }
        }
        br.close();
        connection.close();
    }

    private static void executeSql(Connection connection, String next) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(next)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(next);
            e.printStackTrace();
        }
    }

    private static void importOracleDbScript(String fileName) throws SQLException {
        String sqlCmd = "sqlplus";
        String connectionString = prepareConnectionString(dataSource.getJdbcUrl(), dataSource.getUsername(), dataSource.getPassword());
        ProcessBuilder processBuilder = prepareProcessBuilder(fileName, sqlCmd, connectionString);
        Process sqlPlusProcess;
        try {
            sqlPlusProcess = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try (OutputStream sqlLiteOutput = sqlPlusProcess.getOutputStream();
             PrintWriter sqlPlusCommandLineWriter = new PrintWriter(sqlLiteOutput);
             BufferedReader bri = new BufferedReader(new InputStreamReader(sqlPlusProcess.getInputStream()));
             BufferedReader bre = new BufferedReader(new InputStreamReader(sqlPlusProcess.getErrorStream()))) {

            sqlPlusCommandLineWriter.println("exit");
            sqlPlusCommandLineWriter.flush();

            while (sqlPlusProcess.isAlive()) {
                writeInputAndErrorsToStdOut(bri, bre);
            }
            sqlPlusProcess.destroy();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void writeInputAndErrorsToStdOut(BufferedReader bri, BufferedReader bre) throws IOException {
        String line;
        if ((line = bre.readLine()) != null) {
            System.out.println(line);
        }
        if ((line = bri.readLine()) != null) {
            System.out.println(line);
        }
    }

    private static ProcessBuilder prepareProcessBuilder(String fileName, String sqlCmd, String connectionString) {
        ProcessBuilder pb = new ProcessBuilder(sqlCmd, connectionString, "@" + fileName);
        pb.redirectErrorStream(true);
        pb.redirectInput();
        pb.redirectOutput();
        return pb;
    }

    static String prepareConnectionString(String url, String userName, String password) {
        String[] connectionParams = url.split("@")[1].split(":");
        return userName + "/" + password + "@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(Host=" +
                connectionParams[0] + ")(Port=" + connectionParams[1] + "))(CONNECT_DATA=(SID=" + connectionParams[2] + ")))";
    }

    private static void exportToFile(String arg) {
        try (OutputStream os = new FileOutputStream(arg)) {
            DbToFileExporter.exportTo(os, dataSource);
        } catch (IOException | SQLException e) {
            RecorderLog.error("Could not write to specified file.", e);
        }
    }

    private static void cleanRecordingTables(DataSource dataSource) throws SQLException {
        PreparedStatement dbRecDelete = dataSource.getConnection().prepareStatement("DELETE FROM " + DATABASE_RECORDINGS_TABLE);
        PreparedStatement javaIntRecDelete = dataSource.getConnection().prepareStatement("DELETE FROM " + JAVA_INTERFACE_RECORDINGS_TABLE);
        PreparedStatement httpRecDelete = dataSource.getConnection().prepareStatement("DELETE FROM " + HTTP_RECORDINGS_TABLE);
        dbRecDelete.execute();
        javaIntRecDelete.execute();
        httpRecDelete.execute();
        System.out.println("skuldsku tables cleaned.");
    }

    static String[] getNewArgumentsFromUser(Scanner sc) {
        System.out.println("Usage: rollback | clean | export <file name> | import <file name> | oracleImport <fileName> | runtests <filename> <url>");
        List<String> matchList = new ArrayList<>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher matcher = regex.matcher(sc.nextLine());
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                matchList.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                matchList.add(matcher.group(2));
            } else {
                matchList.add(matcher.group());
            }
        }
        if (matchList.size() == 0) { // user did not enter any command
            getNewArgumentsFromUser(sc);
        }
        String args[] = new String[matchList.size()];
        return matchList.toArray(args);
    }

    // "main" method for testing. Mocks out the data source, the CSV reader and the scanner.
    static void testMain(String[] args, BoneCPDataSource dataSource, Scanner sc, StreamDbPlayBack streamDbPlayBack) throws IOException, SQLException {
        TestRunnerCmd.streamDbPlayBack = streamDbPlayBack;
        prepareDataSource(args, sc);
        TestRunnerCmd.dataSource = dataSource;
        readAndExecuteCommands(args, sc);
    }
}
