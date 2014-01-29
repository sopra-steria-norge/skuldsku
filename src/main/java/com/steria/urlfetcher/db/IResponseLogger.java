package com.steria.urlfetcher.db;

/**
 * specify how to persist data 
 *
 */
public interface IResponseLogger {
	
	void init() throws Exception;

	void destroy();
	
	void persist(String url, String method, String requestHeaders, String requestBody, int responseCode,
			String responseBody, String responseHeaders) throws Exception;
}
