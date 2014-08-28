package no.steria.skuldsku.utils;

/**
 * Represents an entity that can initiate and commit/rollback a database transaction.
 */
public interface TransactionManager {

    /**
     * A method for running a callback within a database transaction.
     * 
     * @param transactionCallback The callback.
     * @return The result returned by the provided callback. 
     */
    public <T> T doInTransaction(TransactionCallback<T> transactionCallback);
    
}
