package com.steria.urlfetcher.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.steria.urlfetcher.RecordedRequestResponse;
import com.steria.urlfetcher.Response;
import com.steria.urlfetcher.UrlFetcher;

public class DbUrlFetcher extends UrlFetcher{
	
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			throw new Exception(
			"Invalid arguments, use UrlFetcher <regexps.xml>");
		}
		new DbUrlFetcher(args[0]);		
	}
	
	DbUrlFetcher(String regexpFile) throws Exception {
		super();
		parseRegExps(regexpFile);
		Connection con = createConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from qtable order by id asc");
		while (rs.next()) {
			String url = rs.getString("url");
			String method = rs.getString("method");
			String headers = rs.getString("headers");
			String body = rs.getString("body");
			//String sessionId = rs.getString("sessionId");
			int responseCode = rs.getInt("responseCode");
			String responseHeaders = rs.getString("responseHeaders");
			String responseBody = rs.getString("responseBody");

			RecordedRequestResponse requestResponse = new RecordedRequestResponse(url, method, headers, body, responseCode, responseHeaders, responseBody);

			Response response = makeCall(requestResponse);
			compareResponse(requestResponse, response);
		}
	}

	private Connection createConnection() throws ClassNotFoundException, SQLException {
		String driverName = System.getProperty("RequestLoggerDBDriver",	"com.mysql.jdbc.Driver");
		String dburl = System.getProperty("RequestLoggerDBUrl", "jdbc:mysql://localhost:3306/mydb");
		String user = System.getProperty("RequestLoggerDBUser", "root");
		String password = System.getProperty("RequestLoggerDBPass");

		Class.forName(driverName);
		System.out.println("Making a connection to: " + dburl);
		Connection con = DriverManager.getConnection(dburl, user, password);
		return con;
	}
}
