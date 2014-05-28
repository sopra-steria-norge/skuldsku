package utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Test;

public class SimpleTransactionManagerTest {

    @Test
    public void successfulDoInTransactionWithCommitAndClose() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        
        final TransactionManager tm = new SimpleTransactionManager(dataSource);
        tm.doInTransaction(new TransactionCallback<String>() {
            @Override
            public String callback(Jdbc jdbc) {
                return null;
            }
        });
        
        verify(connection, times(1)).commit();
        verify(connection, times(1)).close();
    }
    
    @Test
    public void successfulDoInTransactionWithReturn() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        
        final TransactionManager tm = new SimpleTransactionManager(dataSource);
        
        final String TEST_VALUE = "foobar";
        final String result = tm.doInTransaction(new TransactionCallback<String>() {
            @Override
            public String callback(Jdbc jdbc) {
                return TEST_VALUE;
            }
        });
        Assert.assertEquals(TEST_VALUE, result);
        
        verify(connection, times(1)).commit();
        verify(connection, times(1)).close();
    }
    
    @Test
    public void execptionPropagationOk() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenThrow(new SQLException("lala"));
        
        final TransactionManager tm = new SimpleTransactionManager(dataSource);
        
        try {
            tm.doInTransaction(new TransactionCallback<String>() {
                @Override
                public String callback(Jdbc jdbc) {
                    jdbc.execute("NOPE");
                    return null;
                }
            });
            Assert.fail("Expected exception before this statement.");
        } catch (JdbcWrappedException e) {}
        
        verify(connection, times(1)).rollback();
        verify(connection, times(1)).close();
    }
    
    @Test
    public void execptionPropagationOk2() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenThrow(new IllegalStateException("lala"));
        
        final TransactionManager tm = new SimpleTransactionManager(dataSource);
        
        try {
            tm.doInTransaction(new TransactionCallback<String>() {
                @Override
                public String callback(Jdbc jdbc) {
                    jdbc.execute("NOPE");
                    return null;
                }
            });
            Assert.fail("Expected exception before this statement.");
        } catch (IllegalStateException e) {}
        
        
        verify(connection, times(1)).rollback();
        verify(connection, times(1)).close();
    }
    
    @Test(expected=JdbcWrappedException.class)
    public void execptionTranslationOkWhenOpeningConnection() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenThrow(new SQLException("lala"));
        
        final TransactionManager tm = new SimpleTransactionManager(dataSource);
        
        tm.doInTransaction(new TransactionCallback<String>() {
            @Override
            public String callback(Jdbc jdbc) {
                return null;
            }
        });
    }
    
    @Test
    public void missingCallback() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenThrow(new SQLException("lala"));
        
        final TransactionManager tm = new SimpleTransactionManager(dataSource);
        
        try {
            tm.doInTransaction(null);
            Assert.fail("Expected exception before this statement.");
        } catch (NullPointerException e) {}
        
        verify(dataSource, times(0)).getConnection();
    }
}
