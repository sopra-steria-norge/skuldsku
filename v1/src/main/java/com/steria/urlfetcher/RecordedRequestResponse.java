package com.steria.urlfetcher;

public class RecordedRequestResponse implements IRequest{
	private String url;
	private String method;
	private String requestHeaders;
	private String requestBody;
	private int responseCode;
	private String responseHeaders;
	private String responseBody;
	
	public RecordedRequestResponse(String url, String method, String requestHeaders, String requestBody, 
			int responseCode, String responseHeaders, String responseBody) {
		this.url = url;
		this.setMethod(method);
		this.requestHeaders = requestHeaders;
		this.setRequestBody(requestBody);
		this.setResponseCode(responseCode);
		this.responseHeaders = responseHeaders;
		this.responseBody = responseBody;
	}
	
	public RecordedRequestResponse() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(String responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public String getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(String requestHeaders) {
		this.requestHeaders = requestHeaders;
	}



	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
}
