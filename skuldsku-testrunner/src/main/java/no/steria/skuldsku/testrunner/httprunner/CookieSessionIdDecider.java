package no.steria.skuldsku.testrunner.httprunner;

import java.net.HttpCookie;
import java.util.List;

import no.steria.skuldsku.recorder.http.HttpCall;

public final class CookieSessionIdDecider implements SessionIdDecider {
    
    private final String sessionCookieName;
    
    
    public CookieSessionIdDecider() {
        this("JSESSIONID");
    }
    
    public CookieSessionIdDecider(String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }
    
    
    @Override
    public String determineSessionId(HttpCall call) {
        
        List<String> setCookieList = call.getResponseHeaders().get("Set-Cookie");
        if (setCookieList != null) {
            for (String cookie : setCookieList) {
                final List<HttpCookie> hcs = HttpCookie.parse(cookie);
                for (HttpCookie hc : hcs) {
                    if (hc.getName().equals(sessionCookieName)) {
                        return hc.getValue();
                    }
                }
            }
        }
        final List<String> cookieList = call.getHeaders().get("Cookie");
        if (cookieList != null) {
            for (String cookies : cookieList) {
                for (String cookie : cookies.split(";")) {
                    if (cookie.trim().startsWith(sessionCookieName + "=")) {
                        final String value = cookie.substring(cookie.indexOf('=') + 1);
                        return value;
                    }
                }
            }
        }
        return null;
    }
    
}
