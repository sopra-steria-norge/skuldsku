package no.steria.skuldsku.testrunner.dbrunner.dbverifier.verifiers;

import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifier;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseVerifierOptions;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseVerifierResult;

import java.util.ArrayList;
import java.util.List;

public class
        CompositeDatabaseChangeVerifier implements DatabaseChangeVerifier {

    private final DatabaseChangeVerifier[] verifiers;
    
    public CompositeDatabaseChangeVerifier(DatabaseChangeVerifier... verifiers) {
        this.verifiers = verifiers;
    }
    
    @Override
    public DatabaseVerifierResult assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, DatabaseVerifierOptions databaseVerifierOptions) {
        final List<DatabaseVerifierResult> result = new ArrayList<DatabaseVerifierResult>();
        for (DatabaseChangeVerifier verifier : verifiers) {
            result.add(verifier.assertEquals(expected, actual, databaseVerifierOptions));
        }
        return new DatabaseVerifierResult(result);
    }

}
