package no.steria.skuldsku.testrunner.dbrunner.dbverifier.verifiers;

import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifier;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.VerifierOptions;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.VerifierResult;

import java.util.List;

public class StrictOrderDatabaseChangeVerifier implements DatabaseChangeVerifier {

    @Override
    public VerifierResult assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, VerifierOptions verifierOptions) {
        final VerifierResult verifierResult = new VerifierResult();

        if (expected.size() != actual.size()) {
            verifierResult.addAssertionFailed("The actual data should be of the same number of entries as the expected data.");
        } else {
            for (int i=0; i<expected.size(); i++) {
                final DatabaseChange expectedDatabaseChange = expected.get(i);
                final DatabaseChange actualDatabaseChange = actual.get(i);

                final boolean match = expectedDatabaseChange.equals(actualDatabaseChange, verifierOptions.getSkipFields());
                verifierResult.addNotEquals(expectedDatabaseChange, actualDatabaseChange);
                if (!match) {
                    break;
                }
            }
        }
        
        return verifierResult;
    }
}
