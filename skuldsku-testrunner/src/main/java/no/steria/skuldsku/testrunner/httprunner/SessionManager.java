package no.steria.skuldsku.testrunner.httprunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.steria.skuldsku.recorder.logging.RecorderLog;

final class SessionManager {

    private final Map<String, List<PlaybackManipulator>> sessionManipulatorData = new HashMap<>();
    
    
    List<PlaybackManipulator> getSessionPlaybackManipulators(final String sessionId, final List<SessionPlaybackManipulator> sessionManipulators) {
        if (sessionId != null) {
            List<PlaybackManipulator> ms = sessionManipulatorData.get(sessionId);
            if (ms == null) {
                ms = createSessionSpecificPlaybackManipulators(sessionManipulators);
                sessionManipulatorData.put(sessionId, ms);
                RecorderLog.debug("Starting new session: " + sessionId);
            } else {
                RecorderLog.debug("Reusing existing session: " + sessionId);
            }
            return ms;
        } else {
            RecorderLog.debug("Request with no session.");
            return Collections.emptyList();
        }
    }


    private List<PlaybackManipulator> createSessionSpecificPlaybackManipulators(final List<SessionPlaybackManipulator> sessionManipulators) {
        List<PlaybackManipulator> ms;
        ms = new ArrayList<>();
        for (SessionPlaybackManipulator spm : sessionManipulators) {
            ms.add(spm.beginNewSession());
        }
        return ms;
    }
}
