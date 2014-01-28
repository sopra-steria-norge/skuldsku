package utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * A <code>TransactionManager</code> making a new database transaction
 * for each call to {@link #doInTransaction(TransactionCallback)}.
 *
 * <br /><br />
 * 
 * <ol>
 *   <li>A <code>Connection</code> is obtained from the <code>DataSource</code>.</li>
 *   <li>The callback gets executed.</li>
 *   <li>If there are no errors: The transaction gets committed and the connection closed. If there are errors: The transaction get rolled back and the connection closed.</li>
 * </ol>
 */
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
