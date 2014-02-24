package com.steria.urlfetcher;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class CookieMapperTest {

	@Test
	public void updateHeaderEmptyUpdateList() {
		CookieMapper cookieMapper = new CookieMapper();
		cookieMapper.captureResponse(Collections.singletonList(new HeaderLine("Set-Cookie", "host=google.com")));
		ArrayList<HeaderLine> headers = new ArrayList<HeaderLine>();
		headers.add(new HeaderLine("Cookie", "host=vg.no"));
		headers.add(new HeaderLine("Other-Header", "host=vg.no"));
		cookieMapper.updateRequest(headers);
		assertEquals("host=vg.no", headers.get(0).getValue());
	}
		
	@Test
	public void updateHeader() {
		System.setProperty("cookiesToUpdate", "host");
		CookieMapper cookieMapper = new CookieMapper();
		cookieMapper.captureResponse(Collections.singletonList(new HeaderLine("Set-Cookie", "host=google.com")));
		ArrayList<HeaderLine> headers = new ArrayList<HeaderLine>();
		headers.add(new HeaderLine("Cookie", "host=vg.no"));
		headers.add(new HeaderLine("Cookie", "anotherCookie=vg.no"));
		headers.add(new HeaderLine("another-Header", "host=vg.no"));
		cookieMapper.updateRequest(headers);
		assertEquals("host=google.com", headers.get(0).getValue());
	}
	
	@Test
	public void drop() {
		System.setProperty("cookiesToDrop", "host");
		CookieMapper cookieMapper = new CookieMapper();
		cookieMapper.captureResponse(Collections.singletonList(new HeaderLine("Set-Cookie", "host=google.com")));
		List<HeaderLine> headers = new ArrayList<HeaderLine>();
		headers.add(new HeaderLine("Cookie", "host=vg.no"));
		headers.add(new HeaderLine("Cookie", "anotherCookie=vg.no"));
		headers.add(new HeaderLine("another-Header", "host=vg.no"));
		headers = cookieMapper.updateRequest(headers);
		assertEquals(2, headers.size());
	}

}
