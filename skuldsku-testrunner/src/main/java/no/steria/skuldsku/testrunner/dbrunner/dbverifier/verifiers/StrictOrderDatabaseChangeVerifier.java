package no.steria.skuldsku.testrunner.dbrunner.dbverifier.verifiers;

import java.util.List;

import no.steria.skuldsku.common.result.AssertionResult;
import no.steria.skuldsku.common.result.Results;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.FieldDifference;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifier;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifierOptions;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeMatchesResult;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeNotEqualsResult;

public class StrictOrderDatabaseChangeVerifier implements DatabaseChangeVerifier {

    @Override
    public Results assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, DatabaseChangeVerifierOptions databaseChangeVerifierOptions) {
        final Results databaseChangeVerifierResult = new Results();

        for (int i=0; i<Math.min(actual.size(), expected.size()); i++) {
            final DatabaseChange expectedDatabaseChange = expected.get(i);
            final DatabaseChange actualDatabaseChange = actual.get(i);

            final List<FieldDifference> fieldDifferences = expectedDatabaseChange.determineDifferences(actualDatabaseChange, databaseChangeVerifierOptions.getSkipFields());
            if (!fieldDifferences.isEmpty()) {
                databaseChangeVerifierResult.addResult(new DatabaseChangeNotEqualsResult(expectedDatabaseChange, actualDatabaseChange, fieldDifferences));
                break;
            } else if (databaseChangeVerifierOptions.isIncludeSuccessfulMatchesInResult()) {
                databaseChangeVerifierResult.addResult(new DatabaseChangeMatchesResult(expectedDatabaseChange, actualDatabaseChange));
            }
        }

        if (expected.size() != actual.size()) {
            databaseChangeVerifierResult.addResult(new AssertionResult("Database actual result size differs from expected", "The actual data (" + actual.size() + ") should be of the same number of entries as the expected data (" + expected.size() + ")."));
        }
        
        return databaseChangeVerifierResult;
    }
}
