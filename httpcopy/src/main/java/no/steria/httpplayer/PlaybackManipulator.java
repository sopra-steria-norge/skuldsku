package no.steria.httpplayer;

import no.steria.httpspy.ReportObject;

import java.util.List;
import java.util.Map;

public interface PlaybackManipulator {
    public default Map<String, List<String>>  getHeaders(Map<String, List<String>> headers) {
        return headers;
    }

    public default void reportHeaderFields(Map<String, List<String>> headerFields) {};
}
