package no.steria.skuldsku.testrunner.dbrunner.dbverifier;

import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;

import java.util.List;

public interface DatabaseChangeVerifier {

    public DatabaseVerifierResult assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, DatabaseVerifierOptions databaseVerifierOptions);
    
}
 
