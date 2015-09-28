package no.steria.skuldsku.testrunner.httprunner;

public class CookieHandler implements SessionPlaybackManipulator {

    public PlaybackManipulator beginNewSession() {
        return new SessionCookieHandler();
    }
    
}
