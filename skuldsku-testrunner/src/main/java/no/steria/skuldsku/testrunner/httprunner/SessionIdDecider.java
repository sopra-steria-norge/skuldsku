package no.steria.skuldsku.testrunner.httprunner;

import no.steria.skuldsku.recorder.http.HttpCall;

public interface SessionIdDecider {

    String determineSessionId(HttpCall call);
    
}
