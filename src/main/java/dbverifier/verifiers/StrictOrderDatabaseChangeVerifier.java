package dbverifier.verifiers;

import java.util.List;
import java.util.Set;

import dbchange.DatabaseChange;
import dbverifier.DatabaseChangeVerifier;
import dbverifier.VerifierResult;

public class StrictOrderDatabaseChangeVerifier implements DatabaseChangeVerifier {

    @Override
    public VerifierResult assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, Set<String> skipFields) {
        final VerifierResult verifierResult = new VerifierResult();

        if (expected.size() != actual.size()) {
            verifierResult.addAssertionFailed("The actual data should be of the same number of entries as the expected data.");
        } else {
            for (int i=0; i<expected.size(); i++) {
                final DatabaseChange expectedDatabaseChange = expected.get(i);
                final DatabaseChange actualDatabaseChange = actual.get(i);

                final boolean match = expectedDatabaseChange.equals(actualDatabaseChange, skipFields);
                verifierResult.addNotEquals(expectedDatabaseChange, actualDatabaseChange);
                if (!match) {
                    break;
                }
            }
        }
        
        return verifierResult;
    }
}
