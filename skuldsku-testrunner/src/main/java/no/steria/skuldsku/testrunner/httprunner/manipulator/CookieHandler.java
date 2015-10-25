package no.steria.skuldsku.testrunner.httprunner.manipulator;

import no.steria.skuldsku.testrunner.httprunner.PlaybackManipulator;
import no.steria.skuldsku.testrunner.httprunner.SessionPlaybackManipulator;

public class CookieHandler implements SessionPlaybackManipulator {

    public PlaybackManipulator beginNewSession() {
        return new SessionCookieHandler();
    }
    
}
