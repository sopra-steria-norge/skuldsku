package utils;

import java.util.List;

/**
 * Provides methods for executing SQL.
 */
public interface Jdbc {

    public void execute(String sql);
    
    public <T> List<T> queryForList(String sql, Class<T> type, Object... parameters);
    
    public void query(String sql, ResultSetCallback callback, Object... parameters);

}
