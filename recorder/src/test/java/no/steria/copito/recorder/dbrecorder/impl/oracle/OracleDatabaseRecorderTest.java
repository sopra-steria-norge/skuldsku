package no.steria.copito.recorder.dbrecorder.impl.oracle;

import no.steria.copito.utils.Jdbc;
import no.steria.copito.utils.ResultSetCallback;
import no.steria.copito.utils.TransactionCallback;
import no.steria.copito.utils.TransactionManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class OracleDatabaseRecorderTest {


    @Mock
    private Jdbc jdbc;

    @Mock
    TransactionManager transactionManager;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ResultSet resultSet;

    @Before
    public void setUp() {
//        transactionManager = mock(TransactionManager.class, withSettings().verboseLogging());
//        jdbc = mock(Jdbc.class, withSettings().verboseLogging());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldIgnoreSpecifiedTables() {

    }

    @Test
    public void testExecutionWithoutExceptions() {

        final OracleDatabaseRecorder recorder = new OracleDatabaseRecorder(new StubTransactionManager(), new ArrayList<String>(0));
        recorder.initialize();
        recorder.initialize();
        recorder.start();
        recorder.stop();

        final PrintWriter out = new PrintWriter(new StringWriter());
        recorder.exportTo(out);
        recorder.exportAndRemove(out);
        recorder.tearDown();
    }

    @Test
    public void shouldNotCreateTriggersForIgnoredOrCtpTables() throws SQLException {
        List<String> ignoredTables = Arrays.asList("ignored1", "ignored2", "IGNOREd3");
        List<String> allTables = Arrays.asList("table1", "ignored1", "table2", "table3", "ignored2", "ignored3", "cPt_table");
        when(jdbc.queryForList(anyString(), eq(String.class))).thenReturn(allTables); // query for tables
        when(jdbc.queryForList(anyString(), eq(String.class), anyString())).thenReturn(Arrays.asList("tableName")); // query for columns
        OracleDatabaseRecorder recorder = new OracleDatabaseRecorder(transactionManager, ignoredTables);
        recorder.createTriggers(jdbc);

        verify(jdbc, times(1)).execute(startsWith("CREATE OR REPLACE TRIGGER CPT_table1"));
        verify(jdbc, times(1)).execute(startsWith("CREATE OR REPLACE TRIGGER CPT_table2"));
        verify(jdbc, times(1)).execute(startsWith("CREATE OR REPLACE TRIGGER CPT_table3"));
        verify(jdbc, never()).execute(startsWith("CREATE OR REPLACE TRIGGER CPT_ignored1"));
        verify(jdbc, never()).execute(startsWith("CREATE OR REPLACE TRIGGER CPT_ignored2"));
        verify(jdbc, never()).execute(startsWith("CREATE OR REPLACE TRIGGER CPT_ignored3"));
        verify(jdbc, never()).execute(startsWith("CREATE OR REPLACE TRIGGER CPT_cPt_table"));
    }

    private class StubTransactionManager implements TransactionManager {
        @Override
        public <T> T doInTransaction(TransactionCallback<T> transactionCallback) {
            return transactionCallback.callback(new Jdbc() {
                @Override
                public void execute(String sql) {
                }

                @Override
                public <U> List<U> queryForList(String sql, Class<U> type, Object... parameters) {
                    return Collections.emptyList();
                }

                @Override
                public void query(String sql, ResultSetCallback callback, Object... parameters) {
                }
            });
        }
    }
}