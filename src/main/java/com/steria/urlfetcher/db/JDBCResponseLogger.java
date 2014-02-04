package com.steria.urlfetcher.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.annotation.WebFilter;

@WebFilter("/*")
public class JDBCResponseLogger implements IResponseLogger{
	Connection con;
	PreparedStatement stmt;
	private long sequenceNumber;

	@Override
	public void init() throws Exception{
			String driverName = System.getProperty("RequestLoggerDBDriver", "com.mysql.jdbc.Driver");
			String url = System.getProperty("RequestLoggerDBUrl", "jdbc:mysql://localhost:3306/mydb");
			String user = System.getProperty("RequestLoggerDBUser","root");
			String password = System.getProperty("RequestLoggerDBPass");
			
			Class.forName(driverName);			
			
			System.out.println("Making a connection to: " + url);
			
			con = DriverManager.getConnection(url, user, password);
			stmt = con.prepareStatement("INSERT INTO qtable (id, url, method, headers, body, responseCode, responseHeaders, responseBody) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

			//avoid clashes if the server is restarted
			sequenceNumber = Calendar.getInstance().getTimeInMillis();
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
	public void persist(String url, String method, String requestHeaders, String requestBody, String sessionId, 
			int responseCode,	String responseBody, String responseHeaders) throws Exception {
		long id;
		synchronized(this){
			sequenceNumber++;
			id = sequenceNumber;
		}
		stmt.setLong(1, id);
		stmt.setString(2, url);
		stmt.setString(3, method);
		stmt.setString(4, requestHeaders);
		stmt.setString(5, requestBody);
		stmt.setInt(6, responseCode);
		stmt.setString(7, responseHeaders);
		stmt.setString(8, responseBody);
		
		int rst = stmt.executeUpdate();
		if (rst != 1) {
			throw new Exception("Bad result code: " + rst);
		}
	}
}