package com.steria.urlfetcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple utility class for converting and parsing http headers
 */
public class HeaderUtil {

	public static List<HeaderLine> parseHeaders(String headers){
		ArrayList<HeaderLine> h = new ArrayList<HeaderLine>();
		String[] lines = headers.split("\n");
		for(String line : lines){
			String[] segments = line.split(": ",2);
			h.add(new HeaderLine(segments[0], segments[1]));
		}
		return h;
	}
	
	public static String makeCanonic(String headers){
		return makeCanonic(parseHeaders(headers));
	}
	
	public static String makeCanonic(List<HeaderLine> header){
		Collections.sort(header, new HeaderLineKeyComparator());
		String result = "";
		for (HeaderLine hl: header) { 
		   result += hl.getKey() + ": "+ hl.getValue() +"\n";
		}
		return result;
	}
}


