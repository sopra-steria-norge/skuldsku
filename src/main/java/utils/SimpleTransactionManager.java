package utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public final class SimpleTransactionManager implements TransactionManager {
    
    private final DataSource dataSource;
    
    
    public SimpleTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    

    @Override
    public <T> T doInTransaction(TransactionCallback<T> transactionCallback) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            final T result = transactionCallback.callback(new JdbcImpl(connection));
            connection.commit();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            if (connection != null) { 
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    // TODO: Logging.
                    e1.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // TODO: Logging.
                    e.printStackTrace();
                }
            }
        }
    }
}
