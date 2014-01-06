package com.steria.urlfetcher;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.junit.Test;

public class RequestItemTest {

	@Test
	public void parseRequestItem() throws Exception {
		File f = new File("src/test/resources/singleItem.xml");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(RequestItem.class);
		 
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		RequestItem item = (RequestItem) jaxbUnmarshaller.unmarshal(f);
		assertEquals("GET", item.getMethod());
		assertEquals("http://www.vg.no/content/tjenester.php?p=5", item.getUrl());		
	}
}
