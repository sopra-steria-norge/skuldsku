package no.steria.copito.utils;

import java.util.List;

/**
 * Provides methods for executing SQL.
 */
public interface Jdbc {

    /**
     * Executes the provided SQL.
     * @param sql The SQL to be executed.
     */
    public void execute(String sql);
    
    /**
     * Runs the query expecting a single column for each result row.
     * 
     * @param sql The SQL to be executed.
     * @param type The return type.
     * @param parameters Parameters in the SQL with the first entry
     *          matching the first question mark (?) in the SQL. 
     * @return A <code>List</code> with entries of the given type. There's
     *          one entry for each row returned from the database.
     * @throws JdbcException if the result does not have precisely one column,
     *          or for wrapping any <code>SQLException</code>.
     */
    public <T> List<T> queryForList(String sql, Class<T> type, Object... parameters);
    
    /**
     * Runs the query using the provided callback for handling the
     * <code>ResultSet</code>.
     * 
     * @param sql The SQL to be executed.
     * @param callback Callback for handling the <code>ResultSet</code>.
     * @param parameters Parameters in the SQL with the first entry
     *          matching the first question mark (?) in the SQL. 
     * @throws JdbcException if a <code>SQLException</code> is thrown
     *          by the implementation.
     */
    public void query(String sql, ResultSetCallback callback, Object... parameters);

}
