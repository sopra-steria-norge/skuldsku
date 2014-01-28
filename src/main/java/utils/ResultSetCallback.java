package utils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A callback to extract data from a <code>ResultSet</code>.
 */
public interface ResultSetCallback {

    public void extractData(ResultSet resultSet) throws SQLException;
    
}
