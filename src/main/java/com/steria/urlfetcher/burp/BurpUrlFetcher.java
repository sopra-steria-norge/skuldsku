package com.steria.urlfetcher.burp;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.steria.urlfetcher.HeaderUtil;
import com.steria.urlfetcher.Response;
import com.steria.urlfetcher.UrlFetcher;

public class BurpUrlFetcher extends UrlFetcher{
	
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new Exception("Invalid arguments, use BurpUrlFetcher <requests.xml> <regexps.txt>");
		}
		new BurpUrlFetcher(args[1], args[2]);
	}
	
	BurpUrlFetcher(String requestFile, String regexpFile)
			throws Exception {
		List<RegExp> regExps = parseRegExps(regexpFile);
		RequestItems items = parseRequests(requestFile);
		int requestCount = 0;
		for (RequestItem item : items.getItemList()) {
			for (RegExp re : regExps) {
				item.setUrl(re.apply(item.getUrl()));
				item.setHeaders(re.apply(item.getHeaders()));
				// item.setRequest(re.apply(item.getRequest()));
				System.out.println(item.getUrl());
			}
			makeCall(item, "" + requestCount++);
		}
	}
	
	/*
	 * Parse the output file from BurpSuite, effectively a dump of a set of
	 * requests with headers
	 */
	static RequestItems parseRequests(String requestName) throws Exception {
		File file = new File(requestName);
		JAXBContext jaxbContext = JAXBContext.newInstance(RequestItems.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		RequestItems items = (RequestItems) jaxbUnmarshaller.unmarshal(file);
		return items;
	}
	
	static void makeCall(RequestItem requestItem, String outputFilePrefix)
			throws Exception {
		Response response = makeCall(requestItem.getUrl(), requestItem.getMethod(), HeaderUtil.parseHeaders(requestItem.getHeaders()));
		System.out.println(response.getCode());

		if (response.getCode() != 200) {
			return;
		}
		try (PrintWriter printWriter = new PrintWriter(outputFilePrefix + "-"	+ response.getCode())) {
			printWriter.print(response.getBody());
		}
	}
}
