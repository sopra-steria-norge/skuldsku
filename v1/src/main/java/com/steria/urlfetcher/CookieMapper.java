package com.steria.urlfetcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 
 */
public class CookieMapper {
	HashMap<String,String> map = new HashMap<>();
	private List<String> toDrop;
	private List<String> toUpdate;
	
	public CookieMapper(){
		toDrop = new ArrayList<String>(Arrays.asList(System.getProperty("cookiesToDrop", "").split(",")));
		toUpdate = new ArrayList<String>(Arrays.asList(System.getProperty("cookiesToUpdate", "").split(",")));
	}
	
	//replace any Cookie in the stored request with the most up to date value in the replay session
	public List<HeaderLine> updateRequest(List<HeaderLine> headers){
		// example header line
		// Cookie:color=cyan; color2=black
		List<HeaderLine> result = new ArrayList<>();
		for(HeaderLine hl: headers){
			if("Cookie".equals(hl.getKey())){
				List<String> resultValues = new ArrayList<>();
				String[] cookies = hl.getValue().split("; ");
				for(String cookie:cookies){
					String[] cookieSplit = cookie.split("=", 2);
					if(toDrop.contains(cookieSplit[0])){
						continue;
					}
					if(toUpdate.contains(cookieSplit[0])){
						if(map.containsKey(cookieSplit[0])){
							resultValues.add(cookieSplit[0]+"="+map.get(cookieSplit[0]));
						}else{
							System.out.println("Unable to retrieve value for cookie "+cookieSplit[0]);
							resultValues.add(cookie);
						}
					}else{
						resultValues.add(cookie);
					}
				}
				if(resultValues.size() > 0){
					hl.setValue(StringUtils.join(resultValues, "; "));
					result.add(hl);
				}				
			}else{
				result.add(hl);
			}
		}
		return result;
	}
	
	//store away any cookies we receive such that we can replace them with the value 
	public void captureResponse(List<HeaderLine> newHeader){
		for(HeaderLine hl: newHeader){
			if("Set-Cookie".equals(hl.getKey())){
				String[] cookie = hl.getValue().split("=", 2);
				map.put(cookie[0], cookie[1]);
			}
		}		
	}
	
	public void updateRecordedResponseHeaderForCompare(RecordedRequestResponse rec){
		List<HeaderLine> headers = HeaderUtil.parseHeaders(rec.getResponseHeaders());
		for(HeaderLine hl: headers){
			if("Set-Cookie".equals(hl.getKey())){
				String[] cookie = hl.getValue().split("=", 2);
				if(toUpdate.contains(cookie[0])){
					hl.setValue(cookie[0]+"="+map.get(cookie[0]));
				}
			}
		}
		rec.setResponseHeaders(HeaderUtil.makeCanonic(headers));
	}
}
