package com.steria.urlfetcher;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class UrlFetcher {

	public UrlFetcher() {}

	protected static Response makeCall(String url, String method, HashMap<String, String> headers, String body)
			throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod(method);
		for (Entry<String, String> pair : headers.entrySet()) {
			con.setRequestProperty(pair.getKey(), pair.getValue());
		}
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(body);
		wr.flush();
		wr.close();
		
		int responseCode = -1;
		Response response = new Response();
		try{
			responseCode = con.getResponseCode();
		}catch(Exception e){
			System.err.println("Unable to make call to url "+url+" : "+ e);
			return response;
		}
		
		response.setCode(responseCode);
		
		if(responseCode == 200){
			response.setBody(convertStreamToString(con.getInputStream()));
			Map<String, List<String>> headerFields = con.getHeaderFields();
			HashMap<String, String> responseHeaders = new HashMap<>();
			for(Entry<String, List<String>> h: headerFields.entrySet()){
				if(h.getKey() != null){
					responseHeaders.put(h.getKey(), StringUtils.join(h.getValue(), "\n"));
				}
			}
			response.setHeaders(responseHeaders);
		}
		return response;
	}
	
	static String convertStreamToString(java.io.InputStream is) {
    try(java.util.Scanner s = new java.util.Scanner(is)){
    	s.useDelimiter("\\A");
    	return s.hasNext() ? s.next() : "";
    }
	}

	protected static List<RegExp> parseRegExps(String regexpFileName) throws Exception {
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
		return result;
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
