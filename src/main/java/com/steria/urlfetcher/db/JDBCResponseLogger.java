package com.steria.urlfetcher.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.annotation.WebFilter;

@WebFilter("/*")
public class JDBCResponseLogger implements IResponseLogger{
	Connection con;
	PreparedStatement stmt;

	@Override
	public void init() throws Exception{
			String driverName = System.getProperty("RequestLoggerDBDriver", "com.mysql.jdbc.Driver");
			String url = System.getProperty("RequestLoggerDBUrl", "jdbc:mysql://localhost:3306/mydb");
			String user = System.getProperty("RequestLoggerDBUser","root");
			String password = System.getProperty("RequestLoggerDBPass");
			
			Class.forName(driverName);			
			
			System.out.println("Making a connection to: " + url);
			
			con = DriverManager.getConnection(url, user, password);
			stmt = con.prepareStatement("INSERT INTO qtable (url, method, headers, body, responseCode, responseHeaders, responseBody) VALUES (?, ?, ?, ?, ?, ?, ?)");

			System.out.println("Connection successful.\n");
	}

	@Override
	public void destroy() {
		if (con != null) {
			try {
				// Close the connection
				con.close();
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex);
			}
		}
	}

	@SuppressWarnings("nls")
	@Override
	public void persist(String url, String method, String requestHeaders, String requestBody, int responseCode,
			String responseBody, String responseHeaders) throws Exception {
		stmt.setString(1, url);
		stmt.setString(2, method);
		stmt.setString(3, requestHeaders);
		stmt.setString(4, requestBody);
		stmt.setInt(5, responseCode);
		stmt.setString(6, responseHeaders);
		stmt.setString(7, responseBody);
		int rst = stmt.executeUpdate();
		if (rst != 1) {
			throw new Exception("Bad result code: " + rst);
		}
	}
}