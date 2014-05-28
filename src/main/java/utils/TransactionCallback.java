package utils;

/**
 * A callback method to be executed from within a transaction.
 *
 * @param <T> The return type on the callback method.
 */
public interface TransactionCallback<T> {

    /**
     * A callback method to be executed from within a transaction.
     * 
     * @param jdbc The <code>Jdbc</code> instance for executing SQL
     *         within the transaction running this callback.
     * @return The return type and value is defined by the
     *         implementing class. <code>null</code> is used when
     *         no return value is required.
     */
    public T callback(Jdbc jdbc);
    
}
