package no.steria.copito.dbrecorder.dbverifier.verifiers;

import java.util.List;

import no.steria.copito.dbrecorder.dbchange.DatabaseChange;
import no.steria.copito.dbrecorder.dbverifier.DatabaseChangeVerifier;
import no.steria.copito.dbrecorder.dbverifier.VerifierOptions;
import no.steria.copito.dbrecorder.dbverifier.VerifierResult;

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
