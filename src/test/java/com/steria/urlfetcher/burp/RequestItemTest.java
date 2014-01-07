package com.steria.urlfetcher.burp;

import static org.junit.Assert.*;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.steria.urlfetcher.HeaderUtil;

public class RequestItemTest {

	@Test
	public void parseRequestItem() throws Exception {
		File f = new File("src/test/resources/singleItem.xml");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(RequestItem.class);
		 
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		RequestItem item = (RequestItem) jaxbUnmarshaller.unmarshal(f);
		assertEquals("GET", item.getMethod());
		assertEquals("http://www.vg.no/content/tjenester.php?p=5", item.getUrl());
		assertNotNull(item.getRequest());
		assertTrue(item.getHeaders().contains("Accept-Language"));
		assertEquals("en-gb,en;q=0.5", HeaderUtil.parseHeaders(item.getHeaders()).get("Accept-Language"));
	}
}
