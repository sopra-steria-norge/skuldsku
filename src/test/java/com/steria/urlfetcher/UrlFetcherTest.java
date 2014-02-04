package com.steria.urlfetcher;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class UrlFetcherTest {
	UrlFetcher urlFetcher;
	
	@Before
	public void setUp() throws Exception {
		urlFetcher = new UrlFetcher();
		urlFetcher.parseRegExps("src/test/resources/regExps.txt");
	}
	
	@Test
	public void compareResultIdentical() throws Exception {
		RecordedRequestResponse rec = new RecordedRequestResponse();
		rec.setUrl("THE-URL");
		rec.setResponseCode(200);
		rec.setResponseBody("BODY");
		rec.setResponseHeaders("Content-Length: 1482");
		
		Response response = new Response();
		response.setCode(200);
		response.setBody("BODY");
		response.setHeaders(new ArrayList<HeaderLine>(Collections.singletonList(new HeaderLine("Content-Length","1482"))));
		
		assertEquals(0, urlFetcher.compareResponse(rec, response));		
	}
	
	@Test
	public void compareResultCode() throws Exception {
		RecordedRequestResponse rec = new RecordedRequestResponse();
		rec.setUrl("THE-URL");
		rec.setResponseCode(200);
		rec.setResponseBody("BODY");
		rec.setResponseHeaders("Content-Length: 1482");
		
		Response response = new Response();
		response.setCode(404);
		response.setBody("BODY");
		response.setHeaders(new ArrayList<HeaderLine>(Collections.singletonList(new HeaderLine("Content-Length","1482"))));
		
		assertEquals(1, urlFetcher.compareResponse(rec, response));		
	}

	@Test
	public void compareBody() throws Exception {
		RecordedRequestResponse rec = new RecordedRequestResponse();
		rec.setUrl("THE-URL");
		rec.setResponseCode(200);
		rec.setResponseBody("BODY");
		rec.setResponseHeaders("Content-Length: 1482");
		
		Response response = new Response();
		response.setCode(200);
		response.setBody("anotherBODY");
		response.setHeaders(new ArrayList<HeaderLine>(Collections.singletonList(new HeaderLine("Content-Length","1482"))));
		
		assertEquals(4, urlFetcher.compareResponse(rec, response));		
	}
	
	@Test
	public void compareHeader() throws Exception {
		RecordedRequestResponse rec = new RecordedRequestResponse();
		rec.setUrl("THE-URL");
		rec.setResponseCode(200);
		rec.setResponseBody("BODY");
		rec.setResponseHeaders("Content-Length: 1482");
		
		Response response = new Response();
		response.setCode(200);
		response.setBody("BODY");
		response.setHeaders(new ArrayList<HeaderLine>(Collections.singletonList(new HeaderLine("Content-Length","1483"))));
		
		assertEquals(3, urlFetcher.compareResponse(rec, response));		
	}
	
	@Test
	public void compareHeaderCookie() throws Exception {
		System.setProperty("cookiesToUpdate", "ip");
		setUp();
		
		RecordedRequestResponse rec = new RecordedRequestResponse();
		rec.setUrl("THE-URL");
		rec.setResponseCode(200);
		rec.setResponseBody("BODY");
		rec.setResponseHeaders("Set-Cookie: ip=456");
		
		Response response = new Response();
		response.setCode(200);
		response.setBody("BODY");
		response.setHeaders(new ArrayList<HeaderLine>(Collections.singletonList(new HeaderLine("Set-Cookie","ip=123"))));
		
		urlFetcher.cookieMapper.captureResponse(response.getHeaders());
		assertEquals(0, urlFetcher.compareResponse(rec, response));		
	}
}
