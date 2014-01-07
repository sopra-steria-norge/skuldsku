package com.steria.urlfetcher.jetty;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

@WebFilter("/*")
public class ResponseLogger implements Filter {
	Connection con;
	PreparedStatement stmt;
	boolean applyFilter;

	@Override
	public void init(FilterConfig config) throws ServletException {
		try {
			String driverName = System.getProperty("RequestLoggerDBDriver", "com.mysql.jdbc.Driver");
			String url = System.getProperty("RequestLoggerDBUrl", "jdbc:mysql://localhost:3306/mydb");
			String user = System.getProperty("RequestLoggerDBUser","root");
			String password = System.getProperty("RequestLoggerDBPass");
			
			Class.forName(driverName);			
			
			System.out.println("Making a connection to: " + url);
			
			con = DriverManager.getConnection(url, user, password);
			stmt = con.prepareStatement("INSERT INTO qtable (url, method, headers, responseCode, responseHeaders, responseBody) VALUES (?, ?, ?, ?, ?, ?)");

			System.out.println("Connection successful.\n");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		applyFilter = true;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws ServletException, IOException {
		//avoid crashing the server if there are db problems for the filter...
		if(!applyFilter){
			chain.doFilter(request, response);
			return;
		}
		try{
			if (response.getCharacterEncoding() == null) {
				response.setCharacterEncoding("UTF-8"); // Or whatever default. UTF-8 is
																								// good for World Domination.
			}
	
			HttpServletResponseCopier responseCopier = new HttpServletResponseCopier(
					(HttpServletResponse) response);
	
			Request req = (Request) request;
			Response res = (Response) response;
			try {
				chain.doFilter(request, responseCopier);
				responseCopier.flushBuffer();
			} finally {
				byte[] copy = responseCopier.getCopy();
				String responseString = new String(copy, response.getCharacterEncoding());
				log(req, res, responseString);
			}
		}catch(Exception e){
			System.err.println("Exception in logging filter, disabled until next restart: "+e);
			applyFilter = false;
		}
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

	public void log(Request request, Response response, String responseBody) throws Exception {
			String headers = "";
			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String header = headerNames.nextElement();
				headers += header + ": " + request.getHeader(header) + "\n";
			}
			
			String responseHeaders = "";
			Collection<String> responseHeaderNames = response.getHeaderNames();
			for(String header: responseHeaderNames){
				responseHeaders += header + ": " + response.getHeader(header) + "\n";
			}
			
			String method = request.getMethod();
			String query = request.getQueryString();
			if(query == null){
				query = "";
			}
			String r = request.getRequestURL() + query;
			
			doUpdate(r, method, headers, response.getStatus(), responseBody, responseHeaders);
	}

	@SuppressWarnings("nls")
	public void doUpdate(String url, String method, String requestheaders, 
			int responseCode, String responseBody, String responseHeaders) throws Exception {
		int rst;
		
				stmt.setString(1, url);
				stmt.setString(2, method);
				stmt.setString(3, requestheaders);
				stmt.setInt(4, responseCode);
				stmt.setString(5, responseHeaders);
				stmt.setString(6, responseBody);
		rst = stmt.executeUpdate();
		if(rst != 0){
			throw new Exception("Bad result code: "+rst);
		}
	}
}
