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
	
	@Test
	public void parsePostRequestItem() throws Exception {
		File f = new File("src/test/resources/singlePostItem.xml");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(RequestItem.class);
		 
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		RequestItem item = (RequestItem) jaxbUnmarshaller.unmarshal(f);
		assertEquals("POST", item.getMethod());
		assertEquals("http://localhost:9000/mongo/init", item.getUrl());
		assertNotNull(item.getRequest());
		assertTrue(item.getHeaders().contains("Accept-Language"));
		assertEquals("no,nb;q=0.8,en-GB;q=0.6,en;q=0.4,en-US;q=0.2,sv;q=0.2,da;q=0.2", HeaderUtil.parseHeaders(item.getHeaders()).get("Accept-Language"));
		assertEquals("{\"councilFilter\":\"1151\", \"uri\":\"mongodb://localhost:27017/?replicaSet=rep\"}", item.getBody());
	
	}
}
