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
        try {
            final Statement statement = connection.createStatement();
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }
    
    public <T> List<T> queryForList(String sql, Class<T> type, Object... parameters) {
        final List<T> result = new ArrayList<T>();
        query(sql, new ResultSetCallback() {
            @SuppressWarnings("unchecked")
            @Override
            public void extractData(ResultSet resultSet) throws SQLException {
                // TODO: Only execute once for the ResultSet:
                if (resultSet.getMetaData().getColumnCount() != 1) {
                    throw new JdbcException("Expecting single column result, but got multiple columns.");
                }
                final T data = (T) resultSet.getObject(1);
                result.add(data);
            }
        }, parameters);
        return result;
    }
    
    public void query(String sql, ResultSetCallback callback, Object... parameters) {
        try {
            final PreparedStatement statement = connection.prepareStatement(sql);
            for (int i=0; i<parameters.length; i++) {
                statement.setObject(i+1, parameters[i]);
            }
            statement.execute();
            final ResultSet resultSet = statement.getResultSet();
            try {
                while (resultSet.next()) {
                    callback.extractData(resultSet);
                }
            } finally {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    // Logging?
                    e.printStackTrace();
                }
            }
            statement.close();
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }
}
