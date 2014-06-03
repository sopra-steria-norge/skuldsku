package no.steria.copito.testrunner.httpplayer;

import java.util.List;
import java.util.Map;

public interface PlaybackManipulator {
    public default Map<String, List<String>>  getHeaders(Map<String, List<String>> headers) {
        return headers;
    }

    public default String computePayload(String payload) {
        return payload;
    }

    public default void reportHeaderFields(Map<String, List<String>> headerFields) {};

    public default void reportResult(String result) {};
}
