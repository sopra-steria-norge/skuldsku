package no.steria.copito.testrunner;

import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
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

    @Mock
    private SQLExec sqlExec;

    private final String filename = System.getProperty("java.io.tmpdir") + "DatabaseRecorderMainTest.txt";

    @Before
    public void setUp() {
        sqlExec = mock(SQLExec.class, withSettings().verboseLogging());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldWriteToFileWhenRequired() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.getResultSet()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        when(resultSet.getString(anyInt())).thenReturn("column value");

        TestRunnerCmd.testMain(new String[]{
                "dbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb",
                "userId",
                "password",
                "CPT_RECORDER",
                COPITO_DATABASE_TABLE_PREFIX + "JAVA_LOGG",
                "CPT_HTTP_INTERACTIONS_TABLE",
                "export",
                filename}, dataSource);

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
        String userId = "userId";
        String password = "password";
        when(dataSource.getUsername()).thenReturn(userId);
        when(dataSource.getPassword()).thenReturn(password);
        TestRunnerCmd.setSqlExec(sqlExec);
        TestRunnerCmd.testMain(new String[]{
                "dbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb",
                userId,
                password,
                "CPT_RECORDER",
                COPITO_DATABASE_TABLE_PREFIX + "JAVA_LOGG",
                "CPT_HTTP_INTERACTIONS_TABLE",
                "import",
                filename}, dataSource);
        verify(sqlExec, times(1)).setUserid(userId);
        verify(sqlExec, times(1)).setPassword(password);
        verify(sqlExec, times(1)).execute();
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
                "clean"}, dataSource);
        verify(connection, times(1)).prepareStatement("DELETE FROM " + databaseRecordingsTable);
        verify(connection, times(1)).prepareStatement("DELETE FROM " + javaInterfaceRecordingsTable);
        verify(connection, times(1)).prepareStatement("DELETE FROM " + httpInteractionsRecordingsTable);
    }


    @After
    public void cleanUp() {
        File file = new File(filename);
        if(file.exists()) {
            boolean delete = file.delete();
            assertTrue("Could not clean up resources.", delete);
        }
    }
}
