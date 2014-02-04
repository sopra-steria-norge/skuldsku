package com.steria.urlfetcher;

public interface IRequest {
	String getUrl();
	void setUrl(String url);

	String getMethod();
	void setMethod(String method);
	 
	String getRequestHeaders();
	void setRequestHeaders(String requestHeaders);

	String getRequestBody();
	void setRequestBody(String requestBody);
}
