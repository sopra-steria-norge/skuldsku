package no.steria.skuldsku.testrunner.httprunner;

/**
 * Allows session state when manipulating requests.
 */
public interface SessionPlaybackManipulator {

    /**
     * Begins a new session. This method is called once
     * for each session. The returned <code>PlaybackManipulator</code>
     * can store instance variables for data that needs to be kept
     * between requests.
     * 
     * @return A <code>PlaybackManipultor</code> for a new session.
     */
    PlaybackManipulator beginNewSession();
    
}
