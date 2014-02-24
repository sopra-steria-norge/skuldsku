package com.steria.urlfetcher.burp;

import static org.junit.Assert.*;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

public class RequestItemsTest {
	@Test
	public void parseRequestItem() throws Exception {
		File f = new File("src/test/resources/testItems.xml");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(RequestItems.class);
		 
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		RequestItems items = (RequestItems) jaxbUnmarshaller.unmarshal(f);
	
		assertEquals(2,  items.getItemList().size());
	}
}
