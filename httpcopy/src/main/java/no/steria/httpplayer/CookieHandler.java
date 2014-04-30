package no.steria.httpplayer;

import java.util.*;
import java.util.stream.Collectors;

public class CookieHandler implements PlaybackManipulator {
    private Map<String,String> myCookies = new HashMap<>();

    @Override
    public Map<String, List<String>> getHeaders(Map<String, List<String>> headers) {
        if (headers == null) {
            return null;
        }
        Map<String, List<String>> result = cloneMap(headers);

        List<String> cookies = result.get("Cookie");
        if (cookies == null) {
            return result;
        }

        List<String> cookieList = myCookies.entrySet().stream().map(ent -> ent.getKey() + "=" + ent.getValue()).collect(Collectors.toList());
        result.put("Cookie",cookieList);

        return result;

    }

    private Map<String, List<String>> cloneMap(Map<String, List<String>> headers) {
        Map<String, List<String>> result = new HashMap<>();

        for (String key : headers.keySet()) {
            result.put(key,new ArrayList<>(headers.get(key)));
        } return result;
    }

    @Override
    public void reportHeaderFields(Map<String, List<String>> headerFields) {
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
