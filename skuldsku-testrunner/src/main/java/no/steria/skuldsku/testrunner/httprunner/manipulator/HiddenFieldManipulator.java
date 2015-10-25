package no.steria.skuldsku.testrunner.httprunner.manipulator;

import java.util.Collections;
import java.util.Set;

import no.steria.skuldsku.testrunner.httprunner.PlaybackManipulator;
import no.steria.skuldsku.testrunner.httprunner.SessionPlaybackManipulator;

public class HiddenFieldManipulator implements SessionPlaybackManipulator {

    private final String fieldname;
    private final Set<String> ignorePathsRegexs;

    public HiddenFieldManipulator(String fieldname) {
        this(fieldname, Collections.<String>emptySet());
    }
    
    public HiddenFieldManipulator(String fieldname, Set<String> ignorePathsRegexs) {
        this.fieldname = fieldname;
        this.ignorePathsRegexs = ignorePathsRegexs;
    }
    
    @Override
    public PlaybackManipulator beginNewSession() {
        return new SessionHiddenFieldManipulator(fieldname, ignorePathsRegexs);
    }
    
}
