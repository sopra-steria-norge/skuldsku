package com.steria.urlfetcher;

import java.util.List;

/**
 * Represents a parsed http response
 */
public class Response {
	private int code = -1;
	private List<HeaderLine> headers;
	private String body;
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public List<HeaderLine> getHeaders() {
		return headers;
	}
	public void setHeaders(List<HeaderLine> headers) {
		this.headers = headers;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
