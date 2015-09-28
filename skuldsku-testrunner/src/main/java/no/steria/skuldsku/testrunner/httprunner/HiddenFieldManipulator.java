package no.steria.skuldsku.testrunner.httprunner;

public class HiddenFieldManipulator implements SessionPlaybackManipulator {

    private final String fieldname;

    public HiddenFieldManipulator(String fieldname) {
        this.fieldname = fieldname;
    }
    
    @Override
    public PlaybackManipulator beginNewSession() {
        return new SessionHiddenFieldManipulator(fieldname);
    }
    
}
