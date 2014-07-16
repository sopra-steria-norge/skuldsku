package no.steria.copito.testrunner;

import com.jolbox.bonecp.BoneCPDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static no.steria.copito.recorder.Recorder.COPITO_DATABASE_TABLE_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TestRunnerCmdTest {


    @Mock
    private DbToFileExporter dbToFileExporter;

    @Mock
    private BoneCPDataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private final String filename = System.getProperty("java.io.tmpdir") +  TestRunnerCmdTest.class.getCanonicalName() + ".txt";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldWriteToFileWhenRequired() throws SQLException, IOException {
        mockDataSource();
        when(resultSet.next()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false); // one true and one false for each table (runtime this will be different data sets, but for the unit test is is the same class every time)
        when(resultSet.getString(anyInt())).thenReturn("column value");

        TestRunnerCmd.testMain(new String[]{
                "dbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb",
                "userId",
                "password",
                "CPT_RECORDER",
                COPITO_DATABASE_TABLE_PREFIX + "JAVA_LOGG",
                "CPT_HTTP_INTERACTIONS_TABLE",
                "export",
                filename, "exit"}, dataSource, new Scanner(new ByteArrayInputStream(new byte[]{})));

        Scanner scanner = new Scanner(new File(filename));
        String content = scanner.useDelimiter("\\Z").next();
        scanner.close();

        assertEquals("\n **DATABASE RECORDINGS** \n\n" +
                "\"column value\",\"column value\",\"column value\",\"column value\",\"column value\",\"column value\",\"column value\";\n" +
                "\n **JAVA INTERFACE RECORDINGS** \n" +
                "\n\"column value\",\"column value\",\"column value\",\"column value\",\"column value\",\"column value\",\"column value\";\n" +
                "\n **HTTP RECORDINGS** \n\n" +
                "\"column value\",\"column value\",\"column value\",\"column value\",\"column value\";", content);
    }


    @Test
    public void shouldRunDbDump() throws IOException, SQLException {
        prepareSqlScriptTestFile();
        mockDataConnection();
        TestRunnerCmd.testMain(new String[]{
                "dbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb",
                "userId",
                "password",
                "CPT_RECORDER",
                COPITO_DATABASE_TABLE_PREFIX + "JAVA_LOGG",
                "CPT_HTTP_INTERACTIONS_TABLE",
                "import", filename, "exit"}, dataSource, new Scanner(new ByteArrayInputStream(new byte[]{})));
        verifyExpectedSqlWasExecuted();
    }

    @Test
    public void shouldClearCopitoTables() throws IOException, SQLException {
        String databaseRecordingsTable = "CPT_RECORDER";
        String javaInterfaceRecordingsTable = COPITO_DATABASE_TABLE_PREFIX + "JAVA_LOGG";
        String httpInteractionsRecordingsTable = "CPT_HTTP_INTERACTIONS_TABLE";
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        TestRunnerCmd.testMain(new String[]{
                "dbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb",
                "userId",
                "password",
                databaseRecordingsTable,
                javaInterfaceRecordingsTable,
                httpInteractionsRecordingsTable,
                "clean", "exit"}, dataSource, new Scanner(new ByteArrayInputStream(new byte[]{})));
        verify(connection, times(1)).prepareStatement("DELETE FROM " + databaseRecordingsTable);
        verify(connection, times(1)).prepareStatement("DELETE FROM " + javaInterfaceRecordingsTable);
        verify(connection, times(1)).prepareStatement("DELETE FROM " + httpInteractionsRecordingsTable);
    }

    @Test
    public void shouldReadArgumentsCorrectly() {
//        Scanner scanner = new Scanner(new StringInputStream("'these are' some \"commands and\" \"other\"  foo.bar"));
        Scanner scanner = new Scanner(new ByteArrayInputStream("'these are' some \"commands and\" \"other\"  foo.bar".getBytes()));

        String[] newArgumentsFromUser = TestRunnerCmd.getNewArgumentsFromUser(scanner);
        assertEquals("these are", newArgumentsFromUser[0]);
        assertEquals("some", newArgumentsFromUser[1]);
        assertEquals("commands and", newArgumentsFromUser[2]);
        assertEquals("other", newArgumentsFromUser[3]);
        assertEquals("foo.bar", newArgumentsFromUser[4]);
    }

    @Test
    public void shouldBePossibleToExecuteSeveralCommands() throws IOException, SQLException {
        prepareSqlScriptTestFile();
        mockDataSource();
        Scanner scanner = new Scanner(new ByteArrayInputStream(("import " + filename + " export " + filename + " exit").getBytes()));
        TestRunnerCmd.testMain(new String[]{
                "dbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb",
                "userId",
                "password",
                "CPT_RECORDER",
                COPITO_DATABASE_TABLE_PREFIX + "JAVA_LOGG",
                "CPT_HTTP_INTERACTIONS_TABLE"}, dataSource, scanner);
        verify(resultSet, times(3)).next(); //result set called once for each table, thus export was executed.
        verifyExpectedSqlWasExecuted();
      // no stack trace, thus exit was called successfully.
    }

    @Test
    public void shouldCreateCorrectConnectionString() {
        String connectionString = TestRunnerCmd.prepareConnectionString(
                "dbc:oracle:thin:@database.master.com:1521:schema", "userName", "password");
        assertEquals("userName/password@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(Host=database.master.com)(Port=1521))(CONNECT_DATA=(SID=schema)))",
                connectionString);
    }

    @Test
    public void shouldHandleMissingParameters() throws IOException, SQLException {
        String commands = "import\nexport\noracleImport\nexit\n";
        TestRunnerCmd.testMain(new String[]{
                "dbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb",
                "userId",
                "password",
                "databaseRecordingsTable",
                "javaInterfaceRecordingsTable",
                "httpInteractionsRecordingsTable",
                "import"}, dataSource, new Scanner(new ByteArrayInputStream(commands.getBytes())));
    }

    private void verifyExpectedSqlWasExecuted() throws SQLException {
        verify(connection, times(1)).prepareStatement("\ndrop table foo");
        verify(connection, times(1)).prepareStatement(" drop\ntable bar");
        verify(connection, times(1)).prepareStatement(" create table \n" +
                "foo(\"ID\" VARCHAR2(63 BYTE), \n" +
                "\"AUTHOR\" VARCHAR2(63 BYTE), \n" +
                "\"FILENAME\" VARCHAR2(200 BYTE)");
    }

    private void mockDataSource() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.getResultSet()).thenReturn(resultSet);
    }

    private void mockDataConnection() throws SQLException {
        when(dataSource.getJdbcUrl()).thenReturn("dbc:oracle:thin:@database.master.com:1521:schema");
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    private void prepareSqlScriptTestFile() throws IOException {
        File file = new File(filename);
        assertTrue("Could not create file test resource.", file.createNewFile());
        FileOutputStream out = new FileOutputStream(file);
        PrintWriter printWriter = new PrintWriter(out);
        printWriter.write("drop table foo; drop\n" +
                "table bar; create table \nfoo(\"ID\" VARCHAR2(63 BYTE), \n" +
                "\"AUTHOR\" VARCHAR2(63 BYTE), \n" +
                "\"FILENAME\" VARCHAR2(200 BYTE);");
        printWriter.flush();
        out.close();
    }

    @After
    public void cleanUp() {
        File file = new File(filename);
        if (file.exists()) {
            boolean delete = file.delete();
            assertTrue("Could not clean up resources.", delete);
        }
    }
}
