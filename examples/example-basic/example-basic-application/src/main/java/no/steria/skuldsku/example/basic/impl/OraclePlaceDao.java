package no.steria.skuldsku.example.basic.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import no.steria.skuldsku.example.basic.PlaceDao;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

public class OraclePlaceDao implements PlaceDao {
    private static DataSource dataSource = setup();

    private static DataSource setup() {
        try {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } catch (Exception e) {
                throw new IllegalStateException("Oracle driver not found", e);
            }
            OracleConnectionPoolDataSource pool = new OracleConnectionPoolDataSource();
            pool.setURL("jdbc:oracle:thin:@localhost:1521/PDB1");
            pool.setUser("pmuser");
            pool.setPassword("oracle");
            return pool;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    
    public static DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void addPlace(String name) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmnt = conn.prepareStatement("insert into places values (?)")) {
                stmnt.setString(1,name);
                stmnt.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> findMatches(String part) {
        if (part == null) {
            return new ArrayList<>();
        }
        
        final List<String> result = new ArrayList<>();
        String a = "select name from PLACES where upper(name) like '%" + part + "%'";
        try (Connection conn = dataSource.getConnection()) {
            Statement statement = conn.createStatement();
            try (ResultSet resultSet = statement.executeQuery(a)) {
                result.add(resultSet.getString(1)); 
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
