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
import com.steria.urlfetcher.UrlFetcher.RegExp;

public class DbUrlFetcher extends UrlFetcher{
	
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			throw new Exception(
			"Invalid arguments, use UrlFetcher burp <requests.xml> <regexps.txt> or \n" +
			"                       UrlFetcher db <regexps.xml>");
		}
		new DbUrlFetcher(args[1]);		
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
			int responseCode = rs.getInt("responseCode");
			String responseHeaders = rs.getString("responseHeaders");
			String responseBody = rs.getString("responseBody");

			for (RegExp re : regExps) {
				url = re.apply(url);
				headers = re.apply(headers);
				responseHeaders = re.apply(responseHeaders);
				responseBody = re.apply(responseBody);
			}			

			Response response = makeCall(url, method, HeaderUtil.parseHeaders(headers));
			if(response.getCode() != responseCode){
				System.out.println("URL:"+url+" ResponseCode expected "+ responseCode + " got " + response.getCode());
			}else	if(response.getBody() != responseBody){
				System.out.println("URL:"+url+" ResponseBody expected "+ responseBody + " got " + response.getBody());
			}else if(!HeaderUtil.makeCanonic(response.getHeaders()).equals(HeaderUtil.makeCanonic(responseHeaders))){
				System.out.println("URL:"+url+" ResponseHeader expected "+ headers + " got " + response.getHeaders());
			}else{
			  System.out.println("URL:"+ url +" OK");
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
