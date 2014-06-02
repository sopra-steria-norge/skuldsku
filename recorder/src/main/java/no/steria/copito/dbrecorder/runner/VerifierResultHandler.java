package no.steria.copito.dbrecorder.runner;

import no.steria.copito.dbrecorder.dbverifier.VerifierResult;

public interface VerifierResultHandler {

    public void handle(VerifierResult verifierResult);
    
}
