package com.steria.urlfetcher.burp;

import javax.xml.bind.annotation.XmlRootElement;

import com.steria.urlfetcher.IRequest;

/**
 * An "item" in the xml of requests from BurpSuite
 */
@XmlRootElement(name="item")
public
class RequestItem implements IRequest{
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
	private String body;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRequest() {
		return request;
	}
	
	public String getBody(){
		return body;
	}
	
	public void setBody(String body){
		this.body = body;
	}

	// for Burp only
	public void setRequest(String request) {
		//request consists of the headers (key: value) followed by a blank line, optionally followed by a body
		this.request = request;
		int lineBreak = request.indexOf("\n");
		headers = "";
		body = "";
		boolean headerDone = false;
		String[] lines = request.substring(lineBreak+1).split("\n");
		for(String line : lines){
			if("".equals(line)){
				headerDone = true;
			}else{
				if(!headerDone){
					if(!"".equals(headers)){
						headers += "\n";
					}
					headers += line;
				}else{
					if(!"".equals(body)){
						body += "\n";
					}
					body += line;
				}
			}
		}	
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

	@Override
	public String getRequestHeaders() {
		return getHeaders();
	}

	@Override
	public void setRequestHeaders(String requestHeaders) {
		setHeaders(requestHeaders);		
	}

	@Override
	public String getRequestBody() {
		return getBody();
	}

	@Override
	public void setRequestBody(String requestBody) {
		setBody(requestBody);		
	}
}