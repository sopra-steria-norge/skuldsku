package dbrecorder.impl.oracle;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import utils.Jdbc;
import utils.ResultSetCallback;
import utils.TransactionCallback;
import utils.TransactionManager;

public class OracleDatabaseRecorderTest {

    @Test
    public void testExecutionWithoutExceptions() {
        final TransactionManager transactionManager = new TransactionManager() {
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
        };
        
        final OracleDatabaseRecorder impl = new OracleDatabaseRecorder(transactionManager);
        impl.setup();
        impl.setup();
        impl.start();
        impl.stop();
        
        final PrintWriter out = new PrintWriter(new StringWriter());
        impl.exportTo(out);
        impl.exportAndRemove(out);
        impl.tearDown();
    }
}
