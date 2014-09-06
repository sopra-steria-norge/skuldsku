package no.steria.skuldsku.testrunner.dbrunner.dbverifier.verifiers;

import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifier;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseVerifierOptions;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseVerifierResult;

import java.util.List;

public class StrictOrderDatabaseChangeVerifier implements DatabaseChangeVerifier {

    @Override
    public DatabaseVerifierResult assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, DatabaseVerifierOptions databaseVerifierOptions) {
        final DatabaseVerifierResult databaseVerifierResult = new DatabaseVerifierResult();

        if (expected.size() != actual.size()) {
            databaseVerifierResult.addAssertionFailed("The actual data should be of the same number of entries as the expected data.");
        } else {
            for (int i=0; i<expected.size(); i++) {
                final DatabaseChange expectedDatabaseChange = expected.get(i);
                final DatabaseChange actualDatabaseChange = actual.get(i);

                final boolean match = expectedDatabaseChange.equals(actualDatabaseChange, databaseVerifierOptions.getSkipFields());
                databaseVerifierResult.addNotEquals(expectedDatabaseChange, actualDatabaseChange);
                if (!match) {
                    break;
                }
            }
        }
        
        return databaseVerifierResult;
    }
}
