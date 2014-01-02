package utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetCallback {

    public void extractData(ResultSet resultSet) throws SQLException;
    
}
