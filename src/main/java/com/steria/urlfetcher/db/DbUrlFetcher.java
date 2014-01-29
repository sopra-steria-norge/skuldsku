package com.steria.urlfetcher.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.steria.urlfetcher.HeaderUtil;
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
		List<RegExp> regExps = parseRegExps(regexpFile);
		Connection con = createConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from qtable");
		while (rs.next()) {
			String url = rs.getString("url");
			String method = rs.getString("method");
			String headers = rs.getString("headers");
			String body = rs.getString("body");
			int responseCode = rs.getInt("responseCode");
			String responseHeaders = rs.getString("responseHeaders");
			String responseBody = rs.getString("responseBody");

			for (RegExp re : regExps) {
				url = re.apply(url);
				headers = re.apply(headers);
				responseHeaders = re.apply(responseHeaders);
				responseBody = re.apply(responseBody);
			}			

			Response response = makeCall(url, method, HeaderUtil.parseHeaders(headers), body);
			if(response.getCode() != responseCode){
				System.out.println("URL:"+url+" ResponseCode expected "+ responseCode + " got " + response.getCode());
			}else	{
				String newBody = response.getBody();
				String canonicResponseHeaders = HeaderUtil.makeCanonic(responseHeaders);
				String canonicNewHeaders = HeaderUtil.makeCanonic(response.getHeaders());
				if(!responseBody.equals(newBody)){
					System.out.println("URL:"+url+" ResponseBody expected "+ responseBody + " got " + newBody);
				}else if(!canonicNewHeaders.equals(canonicResponseHeaders)){
					System.out.println("URL:"+url+" ResponseHeader expected "+ canonicResponseHeaders + " got " + canonicNewHeaders);
				}else{
				  System.out.println("URL:"+ url +" OK");
				}
			}
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
