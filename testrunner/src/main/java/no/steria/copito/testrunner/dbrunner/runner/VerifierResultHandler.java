package no.steria.copito.testrunner.dbrunner.runner;

import no.steria.copito.testrunner.dbrunner.dbverifier.VerifierResult;

public interface VerifierResultHandler {

    public void handle(VerifierResult verifierResult);
    
}
