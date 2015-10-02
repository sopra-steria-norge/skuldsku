package no.steria.skuldsku.testrunner.dbrunner.dbverifier;

import java.util.List;

import no.steria.skuldsku.common.result.Results;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;

public interface DatabaseChangeVerifier {

    public Results assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, DatabaseChangeVerifierOptions databaseChangeVerifierOptions);
    
}
 
