package no.steria.skuldsku.testrunner.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.steria.skuldsku.testrunner.httprunner.result.HttpCallResult;


public final class HttpClientIdentifierMapper implements ClientIdentifierMapper {

    private final Map<String, String> map;
    
    public HttpClientIdentifierMapper(List<HttpCallResult> results) {
        map = new HashMap<>();
        for (HttpCallResult r : results) {
            map.put(r.getExpected().getClientIdentifier(), r.getActual().getClientIdentifier());
        }
        map.put(null, null);
        map.put("", "");
    }
    
    @Override
    public String translateToActual(String expectedClientIdentifier) {
        final String value = map.get(expectedClientIdentifier);
        return (value != null) ? value : expectedClientIdentifier;
    }
    
}
