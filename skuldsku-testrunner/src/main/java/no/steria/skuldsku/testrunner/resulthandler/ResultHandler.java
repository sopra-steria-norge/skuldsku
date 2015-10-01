package no.steria.skuldsku.testrunner.dbrunner.testrunner;

import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifierResult;

public interface VerifierResultHandler {

    public void handle(DatabaseChangeVerifierResult databaseChangeVerifierResult);

}
