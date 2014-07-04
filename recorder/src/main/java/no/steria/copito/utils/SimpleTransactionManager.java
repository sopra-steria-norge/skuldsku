package no.steria.copito.utils;

import no.steria.copito.recorder.logging.RecorderLog;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

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
    
    
    /**
     * Creates a <code>SimpleTransactionManager</code>.
     * 
     * @param dataSource The <code>DataSource</code> to be used for making
     *          connections to the database when calling
     *          {@link #doInTransaction(TransactionCallback)}.
     */
    public SimpleTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T doInTransaction(TransactionCallback<T> transactionCallback) {
        if (transactionCallback == null) {
            throw new NullPointerException("transactionCallback == null");
        }
        Connection connection = null;
        try {
            connection = getConnection(dataSource);
            final T result = executeCallback(transactionCallback, connection);
            commitTransaction(connection);
            return result;
        } catch (RuntimeException e) {
            rollbackTransaction(connection);
            throw e;
        } finally {
            close(connection);
        }
    }

    /**
     * Opens a <code>Connection</code> to the database.
     * 
     * @param dataSource The <code>DataSource</code> to use for opening
     *          the <code>Connection</code>
     * @return The <code>Connection</code>.
     * @throws JdbcWrappedException if an <code>SQLException</code> occurs while
     *          trying to open the <code>Connection</code>.
     */
    private static Connection getConnection(final DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new JdbcWrappedException("SQLException while trying to get a connection to the database.", e);
        }
    }
    
    private static <T> T executeCallback(TransactionCallback<T> transactionCallback, final Connection connection) {
        return transactionCallback.callback(new JdbcImpl(connection));
    }
    
    private static void commitTransaction(final Connection connection) {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new JdbcWrappedException("SQLException while trying to commit the transaction.", e);
        }
    }
    
    private static void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                // TODO: Logging.
                e1.printStackTrace();
            }
        }
    }
    
    private static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // TODO: Logging.
                RecorderLog.error("Could not close connection.", e);
            }
        }
    }
}
