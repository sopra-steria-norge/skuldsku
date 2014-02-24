package com.steria.urlfetcher;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class UrlFetcher {
	CookieMapper cookieMapper = new CookieMapper();
	List<RegExp> regExps;
	
	public UrlFetcher() {}
	
	protected void setRegExp(List<RegExp> regExp){
		regExps = regExp;
	}

	protected Response makeCall(IRequest rr)
			throws Exception {
		
		List<HeaderLine> headers = prepareRequest(rr);
		
		URL obj = new URL(rr.getUrl());
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod(rr.getMethod());
		for (HeaderLine hl: headers) {
			con.setRequestProperty(hl.getKey(), hl.getValue());
			System.out.println("sending header\n"+hl.getKey()+": "+hl.getValue());
		}
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(rr.getRequestBody());
		wr.flush();
		wr.close();
		
		int responseCode = -1;
		Response response = new Response();
		try{
			responseCode = con.getResponseCode();
		}catch(Exception e){
			System.err.println("Unable to make call to url "+rr.getUrl()+" : "+ e);
			return response;
		}
		
		response.setCode(responseCode);		
		if(responseCode == 200){
			response.setBody(convertStreamToString(con.getInputStream()));
			Map<String, List<String>> headerFields = con.getHeaderFields();
			List<HeaderLine> responseHeaders = new ArrayList<>();
			for(Entry<String, List<String>> h: headerFields.entrySet()){
				if(h.getKey() != null){
					for(String value:h.getValue()){
						responseHeaders.add(new HeaderLine(h.getKey(), value));
					}
				}
			}
			response.setHeaders(responseHeaders);
			cookieMapper.captureResponse(responseHeaders);
		}
		return response;
	}

	//modify the recorded data before making request
	private List<HeaderLine> prepareRequest(IRequest rr) {
		//apply regular expressions
		for (RegExp re : regExps) {
			rr.setUrl(re.apply(rr.getUrl()));
			rr.setRequestHeaders(re.apply(rr.getRequestHeaders()));
			rr.setRequestBody(re.apply(rr.getRequestBody()));
		}
		// and modify cookies
		List<HeaderLine> headers = HeaderUtil.parseHeaders(rr.getRequestHeaders());
		headers = cookieMapper.updateRequest(headers);
		return headers;
	}
	
	//modify recorded response before comparing to actual response
	private void prepareResponse(RecordedRequestResponse rec) {
		for (RegExp re : regExps) {
			rec.setResponseHeaders(re.apply(rec.getResponseHeaders()));
		}
		cookieMapper.updateRecordedResponseHeaderForCompare(rec);
	}
	
	protected int compareResponse(RecordedRequestResponse rec, Response response) {
		prepareResponse(rec);
		if(response.getCode() != rec.getResponseCode()){
			System.out.println("URL:"+rec.getUrl()+" ResponseCode expected "+ rec.getResponseCode() + " got " + response.getCode());
			return 1;
		}else	{
			String newBody = response.getBody();
			
			String canonicResponseHeaders = HeaderUtil.makeCanonic(rec.getResponseHeaders());
			String canonicNewHeaders = HeaderUtil.makeCanonic(response.getHeaders());
			if(!rec.getResponseBody().equals(newBody)){
				System.out.println("URL:"+rec.getUrl()+" ResponseBody expected "+ rec.getResponseBody() + " got " + newBody);
				return 4;
			}else if(!canonicNewHeaders.equals(canonicResponseHeaders)){
				System.out.println("URL:"+rec.getUrl()+" ResponseHeader expected "+ canonicResponseHeaders + " got " + canonicNewHeaders);
				return 3;
			}else{
			  System.out.println("URL:"+ rec.getUrl() +" OK");
			  System.out.println("old\n"+canonicResponseHeaders+"\nnew\n"+canonicNewHeaders);
			  return 0;
			}
		}
	}
	
	static String convertStreamToString(java.io.InputStream is) {
    try(java.util.Scanner s = new java.util.Scanner(is)){
    	s.useDelimiter("\\A");
    	return s.hasNext() ? s.next() : "";
    }
	}

	protected void parseRegExps(String regexpFileName) throws Exception {
		List<RegExp> result = new ArrayList<>();
		File file = new File(regexpFileName);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] split = line.split("===");
			if(split.length == 1){
				result.add(new RegExp(split[0], ""));
			}else{
				result.add(new RegExp(split[0], split[1]));
			}
		}
		fileReader.close();
		regExps = result;
	}	

	// A grouping of a regexp pattern and the replace blob
	public static class RegExp {
		private Pattern pattern;
		private String replace;

		RegExp(String patternString, String replace) {
			this.pattern = Pattern.compile(patternString);
			this.replace = replace;
		}

		public String apply(String url) {
			return pattern.matcher(url).replaceAll(replace);
		}
	}
}
