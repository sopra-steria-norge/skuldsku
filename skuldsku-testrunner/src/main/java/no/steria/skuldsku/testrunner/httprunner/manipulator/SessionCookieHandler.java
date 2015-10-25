package no.steria.skuldsku.testrunner.httprunner.manipulator;

import java.util.*;
import java.util.stream.Collectors;

import no.steria.skuldsku.testrunner.httprunner.PlaybackManipulator;
import no.steria.skuldsku.testrunner.httprunner.RequestCompleteData;
import no.steria.skuldsku.testrunner.httprunner.RequestData;

class SessionCookieHandler implements PlaybackManipulator {
    private Map<String,String> myCookies = new HashMap<>();
    
    public void performRequestManipulation(RequestData requestData) {
        updateHeadersWithMyCookies(requestData.getRequestHeaders());
    }
    
    public void reportRequestCompleteData(RequestCompleteData requestCompleteData) {
        storeMyCookies(requestCompleteData.getResponseHeaders());
    }

    
    private void updateHeadersWithMyCookies(final Map<String, List<String>> requestHeaders) {
        if (requestHeaders == null) {
            /* No request headers in original request. */
            return;
        }
        
        if (!requestHeaders.containsKey("Cookie")) {
            /* No cookies were sent in the original request. */
            return;
        }
        
        final List<String> cookieList = myCookies.entrySet().stream().map(ent -> ent.getKey() + "=" + ent.getValue()).collect(Collectors.toList());
        requestHeaders.put("Cookie",cookieList);
    }

    private void storeMyCookies(Map<String, List<String>> headerFields) {
        List<String> cookies = headerFields.get("Set-Cookie");
        if (cookies != null) {
            for (String cookieStr : cookies) {
                String[] parts = cookieStr.split(";");
                if (parts == null) {
                    continue;
                }
                for (String part : parts) {
                    int ind = part.indexOf("=");
                    if (ind < 0) {
                        continue;
                    }
                    String cookieName = part.substring(0,ind);
                    if ("expires".equalsIgnoreCase(cookieName) || "domain".equalsIgnoreCase(cookieName) || "path".equalsIgnoreCase(cookieName)) {
                        continue;
                    }
                    myCookies.put(cookieName,part.substring(ind+1));
                }
            }
        }

    }
}
