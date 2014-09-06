package no.steria.skuldsku.testrunner.dbrunner.testrunner;

import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseVerifierResult;

public interface VerifierResultHandler {

    public void handle(DatabaseVerifierResult databaseVerifierResult);

}
