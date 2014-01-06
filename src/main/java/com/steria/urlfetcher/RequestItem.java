package com.steria.urlfetcher;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * An "item" in the xml of requests from BurpSuite
 */
@XmlRootElement
class RequestItem{
	private String url;
	private String request;
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

	public void setRequest(String request) {
		this.request = request;
	}
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public HashMap<String, String> getHeaders(){
		HashMap<String, String> result = new HashMap<>();
		String[] lines = getRequest().split("\n");
		for(int i=1; i<lines.length;i++){ // skip first line
			String line = lines[i];
			String[] segments = line.split(":",2);
				result.put(segments[0], segments[1]);
		}
		return result;
	}
}