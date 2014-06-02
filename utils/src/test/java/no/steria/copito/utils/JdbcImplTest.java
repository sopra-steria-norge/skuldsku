package no.steria.copito.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class JdbcImplTest {

    @Test
    public void executeSuccessfully() throws Exception {
        final Connection connection = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);
        
        final String SQL = "SELECT FOO FROM BAR";
        final Jdbc jdbc = new JdbcImpl(connection);
        jdbc.execute(SQL);
        
        verify(connection, times(1)).createStatement();
        verify(statement, times(1)).execute(SQL);
        verify(statement, times(1)).close();
    }
    
    @Test
    public void executeWithSQLException() throws Exception {
        final String SQL = "SELECT FOO FROM BAR";
        
        final Connection connection = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(SQL)).thenThrow(new SQLException("foobar"));
        
        final Jdbc jdbc = new JdbcImpl(connection);
        try {
            jdbc.execute(SQL);
            Assert.fail("Expected JdbcException before this statement.");
        } catch (JdbcException e) {}
        
        verify(connection, times(1)).createStatement();
        verify(statement, times(1)).execute(SQL);
        verify(statement, times(1)).close();
    }
    
    @Test
    public void executeWithRuntimeException() throws Exception {
        final String SQL = "SELECT FOO FROM BAR";
        
        final Connection connection = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(SQL)).thenThrow(new IllegalStateException("foobar"));
        
        final Jdbc jdbc = new JdbcImpl(connection);
        try {
            jdbc.execute(SQL);
            Assert.fail("Expected IllegalStateException before this statement.");
        } catch (IllegalStateException e) {}
        
        verify(connection, times(1)).createStatement();
        verify(statement, times(1)).execute(SQL);
        verify(statement, times(1)).close();
    }
    
    @Test
    public void executeWithoutSql() throws Exception {
        final Connection connection = mock(Connection.class);
        
        final Jdbc jdbc = new JdbcImpl(connection);
        try {
            jdbc.execute(null);
            Assert.fail("Expected NullPointerException before this statement.");
        } catch (NullPointerException e) {};
        
        verifyZeroInteractions(connection);
    }
    
    @Test
    public void queryForListWithEmptyResult() throws Exception {
        final String SQL = "SELECT FOO FROM BAR";
        
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);
        final ResultSetMetaData metaData = mock(ResultSetMetaData.class);
       
        when(connection.prepareStatement(SQL)).thenReturn(statement);
        when(statement.getResultSet()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(1);
        
        final Jdbc jdbc = new JdbcImpl(connection);
        final List<String> result = jdbc.queryForList(SQL, String.class);
        
        Assert.assertEquals(0, result.size());
        verify(connection, times(1)).prepareStatement(SQL);
        verify(statement, times(1)).execute();
        verify(statement, times(1)).close();
        verify(resultSet, times(1)).close();
    }
    
    @Test
    public void queryForLisWithoutSql() throws Exception {
        final Connection connection = mock(Connection.class);
        
        final Jdbc jdbc = new JdbcImpl(connection);
        try {
            jdbc.queryForList(null, String.class);
            Assert.fail("Expected NullPointerException before this statement.");
        } catch (NullPointerException e) {};
        
        verifyZeroInteractions(connection);
    }
    
    @Test
    public void queryForLisWithoutReturnType() throws Exception {
        final Connection connection = mock(Connection.class);
        
        final Jdbc jdbc = new JdbcImpl(connection);
        try {
            jdbc.queryForList("SELECT FOO FROM BAR", null);
            Assert.fail("Expected NullPointerException before this statement.");
        } catch (NullPointerException e) {};
        
        verifyZeroInteractions(connection);
    }
    
    @Test
    public void queryForListWithOneRow() throws Exception {
        final String SQL = "SELECT FOO FROM BAR";
        final String QUERY_RESULT = "FOOBAR";
        
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);
        final ResultSetMetaData metaData = mock(ResultSetMetaData.class);
       
        when(connection.prepareStatement(SQL)).thenReturn(statement);
        when(statement.getResultSet()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(1);
        
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getObject(1)).thenReturn(QUERY_RESULT);
        
        final Jdbc jdbc = new JdbcImpl(connection);
        final List<String> result = jdbc.queryForList(SQL, String.class);
        
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(QUERY_RESULT, result.get(0));
        verify(connection, times(1)).prepareStatement(SQL);
        verify(statement, times(1)).execute();
        verify(statement, times(1)).close();
        verify(resultSet, times(1)).close();
    }
    
    @Test
    public void queryForListWithMultipleRows() throws Exception {
        final String SQL = "SELECT FOO FROM BAR";
        final String QUERY_RESULT1 = "FOOBAR";
        final String QUERY_RESULT2 = "TEST";
        
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);
        final ResultSetMetaData metaData = mock(ResultSetMetaData.class);
       
        when(connection.prepareStatement(SQL)).thenReturn(statement);
        when(statement.getResultSet()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(1);
        
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(QUERY_RESULT1, QUERY_RESULT2);
        
        final Jdbc jdbc = new JdbcImpl(connection);
        final List<String> result = jdbc.queryForList(SQL, String.class);
        
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(QUERY_RESULT1, result.get(0));
        Assert.assertEquals(QUERY_RESULT2, result.get(1));
        verify(connection, times(1)).prepareStatement(SQL);
        verify(statement, times(1)).execute();
        verify(statement, times(1)).close();
        verify(resultSet, times(1)).close();
    }
    
    @Test
    public void queryForListWithBindVariabled() throws Exception {
        final String SQL = "SELECT FOO FROM BAR";
        final String QUERY_RESULT = "FOOBAR";
        
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);
        final ResultSetMetaData metaData = mock(ResultSetMetaData.class);
       
        when(connection.prepareStatement(SQL)).thenReturn(statement);
        when(statement.getResultSet()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(1);
        
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getObject(1)).thenReturn(QUERY_RESULT);
        
        final Jdbc jdbc = new JdbcImpl(connection);
        final String PARAM_1 = "foo";
        final int PARAM_2 = 42;
        final boolean PARAM_3 = true;
        final List<String> result = jdbc.queryForList(SQL, String.class, PARAM_1, PARAM_2, PARAM_3);
        
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(QUERY_RESULT, result.get(0));
        verify(connection, times(1)).prepareStatement(SQL);
        verify(statement, times(1)).setObject(1, PARAM_1);
        verify(statement, times(1)).setObject(2, 42);
        verify(statement, times(1)).setObject(3, PARAM_3);
        verify(statement, times(1)).execute();
        verify(statement, times(1)).close();
        verify(resultSet, times(1)).close();
    }
    
    @Test
    public void queryForListWrongColumnLength() throws Exception {
        final String SQL = "SELECT FOO, TEST FROM BAR";
        
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);
        final ResultSetMetaData metaData = mock(ResultSetMetaData.class);
       
        when(connection.prepareStatement(SQL)).thenReturn(statement);
        when(statement.getResultSet()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(2);
        
        final Jdbc jdbc = new JdbcImpl(connection);
        try {
            jdbc.queryForList(SQL, String.class);
            Assert.fail("Expected JdbcException before this statement.");
        } catch (JdbcException e) {}
        
        verify(connection, times(1)).prepareStatement(SQL);
        verify(statement, times(1)).execute();
        verify(statement, times(1)).close();
        verify(resultSet, times(1)).close();
    }
    
    @Test
    public void querWithoutSql() throws Exception {
        final Connection connection = mock(Connection.class);
        
        final Jdbc jdbc = new JdbcImpl(connection);
        try {
            jdbc.queryForList(null, String.class);
            Assert.fail("Expected NullPointerException before this statement.");
        } catch (NullPointerException e) {};
        
        verifyZeroInteractions(connection);
    }
    
    @Test
    public void queryWithoutSql() throws Exception {
        final Connection connection = mock(Connection.class);
        
        final Jdbc jdbc = new JdbcImpl(connection);
        try {
            jdbc.query(null, new ResultSetCallback() {
                @Override
                public void extractData(ResultSet resultSet) throws SQLException {
                }
            });
            Assert.fail("Expected NullPointerException before this statement.");
        } catch (NullPointerException e) {};
        
        verifyZeroInteractions(connection);
    }
    
    @Test
    public void queryWithoutCallback() throws Exception {
        final Connection connection = mock(Connection.class);
        
        final Jdbc jdbc = new JdbcImpl(connection);
        try {
            jdbc.query("SELECT FOO FROM BAR", null);
            Assert.fail("Expected NullPointerException before this statement.");
        } catch (NullPointerException e) {};
        
        verifyZeroInteractions(connection);
    }
}
