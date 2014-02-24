package com.steria.urlfetcher.proxy;

import java.net.URI;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import com.steria.urlfetcher.jetty.ResponseFilter;

/**
 * Record request / response using a standalone proxy
 */
@SuppressWarnings("serial")
public class Proxy extends ProxyServlet {
	
	static String target;
	
	@Override
	protected URI rewriteURI(HttpServletRequest request) {
    String scheme = request.getScheme();
    String path = request.getPathInfo();
    String query = request.getQueryString();
    if(query != null){
    	query = "?"+query;
    }else{
    	query="";
    }
    
    return URI.create(scheme+"://"+target+ path + query);
	}
	
	@Override
	protected void customizeProxyRequest(Request proxyRequest, HttpServletRequest request) {
		proxyRequest.getHeaders().remove("Host");
	}

	
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			throw new Exception("Invalid arguments, use Proxy <redirect host:port>");
		}
		target = args[0];
		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(Proxy.class, "/*");
		handler.addFilterWithMapping(ResponseFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		
		SessionHandler sh = new SessionHandler();
		sh.setHandler(handler);

		Server server = new Server(8080);
		server.setHandler(sh);	

		server.start();
		server.join();
	}

}
