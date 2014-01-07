package com.steria.urlfetcher.burp;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

import com.steria.urlfetcher.HeaderUtil;

/**
 * An "item" in the xml of requests from BurpSuite
 */
@XmlRootElement(name="item")
class RequestItem{
	private String url;
	private String request;
	private String headers;
	private String method;
	//Not used, but available if needed later
  //private String protocol;
  //private String time;
	//private String port;
	//private String path;
	//private String extension;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRequest() {
		return request;
	}

	// for Burp only
	public void setRequest(String request) {
		this.request = request;
		headers = request.substring(request.indexOf("\n")+1);
	}
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getHeaders(){		
		return headers;
	}
	
	public void setHeaders(String headers){
		this.headers = headers;
	}
}