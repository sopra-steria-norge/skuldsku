package com.steria.urlfetcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class UrlFetcher {

	public static void main(String[] args) throws Exception {
		
		if(args.length != 2){
			throw new Exception("Invalid arguments, use UrlFetcher <requests.xml> <regexps.txt>");
		}
		new UrlFetcher(args[0],  args[1]);
		//RegExp re = new RegExp("abc","cde");		
	}
	
	public UrlFetcher(String requestFile, String regexpFile) throws Exception{
		List<RegExp> regExps = parseRegExps(regexpFile);
		RequestItems items = parseRequests(requestFile);
		int requestCount = 0;
		for(RequestItem item : items.getItemList()){
			for(RegExp re: regExps){
				item.setUrl(re.apply(item.getUrl()));
				item.setRequest(re.apply(item.getRequest()));
				System.out.println(item.getUrl());
			}
			makeCall(item, ""+requestCount++);
		}
	}
	
	void makeCall(RequestItem requestItem, String outputFilePrefix) throws Exception{
		URL obj = new URL(requestItem.getUrl());
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod(requestItem.getMethod());
		for(Entry<String, String> pair: requestItem.getHeaders().entrySet()){
			String value = pair.getValue();
			//retrieve uncompressed, makes diff meaningful...
			if(pair.getKey().equals("Accept-Encoding")){
				pair.setValue(value.replace("gzip, ", ""));
				pair.setValue(value.replace("gzip", ""));
			}
			con.setRequestProperty(pair.getKey(), pair.getValue());
		}
		int responseCode = con.getResponseCode();
		System.out.println(responseCode);
		
		try(PrintWriter printWriter = new PrintWriter(outputFilePrefix+"-"+responseCode)){
			if(responseCode != 200){
				return;
			}
			try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))){
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					printWriter.println(inputLine);
				}
			}
		}
	}
	
	List<RegExp> parseRegExps(String regexpFileName) throws Exception{
		List<RegExp> result = new ArrayList<>();
		File file = new File(regexpFileName);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] split = line.split("===");
			result.add(new RegExp(split[0],  split[1]));
		}
		fileReader.close();
		return result;
	}
	
	/*
	 * Parse the output file from BurpSuite, effectively a dump of a set of requests with headers 
	 */
	RequestItems parseRequests(String requestName) throws Exception
	{
		File file = new File(requestName);
		JAXBContext jaxbContext = JAXBContext.newInstance(RequestItems.class);
 
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		RequestItems items = (RequestItems) jaxbUnmarshaller.unmarshal(file);
		return items;
	}
	
	// A grouping of a regexp pattern and the replace blob
	static class RegExp{
		private Pattern pattern;
		private String replace;
		RegExp(String patternString, String replace){
			this.pattern = Pattern.compile(patternString);
			this.replace = replace;
		}
		
		String apply(String url){
			return pattern.matcher(url).replaceAll(replace);
		}
	}
}



