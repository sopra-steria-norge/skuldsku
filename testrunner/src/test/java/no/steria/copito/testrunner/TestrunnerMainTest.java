package no.steria.copito.testrunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import static no.steria.copito.recorder.Recorder.COPITO_DATABASE_TABLE_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class TestRunnerMainTest {


    @Mock
    private DbToFileExporter dbToFileExporter;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private String filename;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldWriteToFileWhenRequired() throws Exception {
        filename = System.getProperty("java.io.tmpdir") + "DatabaseRecorderMainTest.txt";
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.getResultSet()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        when(resultSet.getString(anyInt())).thenReturn("column value");

        TestRunnerMain.testMain(new String[]{
                "dbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb",
                "wimpel_dba",
                "wimpel",
                "export",
                filename,
                "CPT_RECORDER",
                COPITO_DATABASE_TABLE_PREFIX + "JAVA_LOGG",
                "CPT_HTTP_INTERACTIONS_TABLE"}, dataSource);

        Scanner scanner = new Scanner(new File(filename));
        String content = scanner.useDelimiter("\\Z").next();
        scanner.close();

        assertEquals(" **DATABASE RECORDINGS** \"column value\",\"column value\",\"column value\",\"column value\",\"column value\",\"column value\",\"column value\"; **JAVA INTERFACE RECORDINGS** \"column value\",\"column value\",\"column value\",\"column value\",\"column value\",\"column value\",\"column value\"; **HTTP RECORDINGS** \"column value\",\"column value\",\"column value\",\"column value\",\"column value\";", content);
    }

    @Ignore("This test runs against the database")
    @Test
    public void shouldExportDataFromDb() throws FileNotFoundException {
        filename = "";
        TestRunnerMain.main(new String[]{
                "dbc:oracle:thin:@slfutvdb1.master.no:1521:slfutvdb",
                "wimpel_dba",
                "****",
                "export",
                "C:\\tmp\\testfile.txt",
                "CPT_RECORDER",
                COPITO_DATABASE_TABLE_PREFIX + "JAVA_LOGG",
                "CPT_HTTP_INTERACTIONS_TABLE",
        });
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
