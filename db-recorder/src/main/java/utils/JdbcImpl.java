package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class JdbcImpl implements Jdbc {

    private final Connection connection;
    
    public JdbcImpl(Connection connection) {
        this.connection = connection;
    }
    
    public void execute(String sql) {
        if (sql == null) {
            throw new NullPointerException("sql == null");
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            throw new JdbcWrappedException(e);
        } finally {
            closeStatement(statement);
        }
    }
    
    public <T> List<T> queryForList(String sql, Class<T> type, Object... parameters) {
        if (sql == null) {
            throw new NullPointerException("sql == null");
        }
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        final List<T> result = new ArrayList<T>();
        query(sql, new ResultSetCallback() {
            @SuppressWarnings("unchecked")
            @Override
            public void extractData(ResultSet resultSet) throws SQLException {
                if (resultSet.getMetaData().getColumnCount() != 1) {
                    throw new JdbcException("Expecting single column result, but got multiple columns.");
                }
                while (resultSet.next()) {
                    final T data = (T) resultSet.getObject(1);
                    result.add(data);
                }
            }
        }, parameters);
        return result;
    }
    
    public void query(String sql, ResultSetCallback callback, Object... parameters) {
        if (sql == null) {
            throw new NullPointerException("sql == null");
        }
        if (callback == null) {
            throw new NullPointerException("callback == null");
        }
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            for (int i=0; i<parameters.length; i++) {
                statement.setObject(i+1, parameters[i]);
            }
            statement.execute();
            ResultSet resultSet = null;
            try {
                resultSet = statement.getResultSet();
                callback.extractData(resultSet);
            } finally {
                closeResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new JdbcWrappedException(e);
        } finally {
            closeStatement(statement);
        }
    }
    
    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                // Logging?
            }
        }
    }
    
    private void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                // Logging?
            }
        }
    }
}
