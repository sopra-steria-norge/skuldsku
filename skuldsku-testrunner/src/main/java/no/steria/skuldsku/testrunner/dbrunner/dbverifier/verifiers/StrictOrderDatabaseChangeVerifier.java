package no.steria.skuldsku.testrunner.dbrunner.dbverifier.verifiers;

import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifier;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifierOptions;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifierResult;

import java.util.List;

public class StrictOrderDatabaseChangeVerifier implements DatabaseChangeVerifier {

    @Override
    public DatabaseChangeVerifierResult assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, DatabaseChangeVerifierOptions databaseChangeVerifierOptions) {
        final DatabaseChangeVerifierResult databaseChangeVerifierResult = new DatabaseChangeVerifierResult();

        if (expected.size() != actual.size()) {
            databaseChangeVerifierResult.addAssertionFailed("The actual data should be of the same number of entries as the expected data.");
        } else {
            for (int i=0; i<expected.size(); i++) {
                final DatabaseChange expectedDatabaseChange = expected.get(i);
                final DatabaseChange actualDatabaseChange = actual.get(i);

                final boolean match = expectedDatabaseChange.equals(actualDatabaseChange, databaseChangeVerifierOptions.getSkipFields());
                databaseChangeVerifierResult.addNotEquals(expectedDatabaseChange, actualDatabaseChange);
                if (!match) {
                    break;
                }
            }
        }
        
        return databaseChangeVerifierResult;
    }
}
