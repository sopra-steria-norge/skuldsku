package com.steria.urlfetcher;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Simple utility class for converting and parsing http headers
 */
public class HeaderUtil {

	public static HashMap<String, String> parseHeaders(String headers){
		HashMap<String, String> result = new HashMap<>();
		String[] lines = headers.split("\n");
		for(String line : lines){
			String[] segments = line.split(": ",2);
			result.put(segments[0], segments[1]);
		}
		return result;
	}
	
	public static String makeCanonic(String headers){
		return makeCanonic(parseHeaders(headers));
	}
	
	public static String makeCanonic(HashMap<String, String> headers){
		String result = "";
		SortedSet<String> keys = new TreeSet<String>(headers.keySet());
		for (String key : keys) { 
		   result += key + ": "+ headers.get(key)+"\n";
		}
		return result;
	}
}
