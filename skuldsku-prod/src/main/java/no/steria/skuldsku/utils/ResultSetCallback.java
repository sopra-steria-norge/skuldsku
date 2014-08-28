package no.steria.skuldsku.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A callback to extract data from a <code>ResultSet</code>.
 */
public interface ResultSetCallback {

    /**
     * This method should be handling the data from provided result set.
     * 
     * <pre>
     * while (resultSet.next()) {
     *    resultSet.get...
     * }
     * </pre>
     * 
     * @param resultSet The <code>ResultSet</code> with the data to be extracted.
     * @throws SQLException The implementation may throw an SQLException in
     *          case of an SQL error.
     */
    public void extractData(ResultSet resultSet) throws SQLException;
    
}
