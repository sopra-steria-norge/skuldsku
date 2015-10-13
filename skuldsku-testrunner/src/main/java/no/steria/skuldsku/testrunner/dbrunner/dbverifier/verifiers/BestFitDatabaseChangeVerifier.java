package no.steria.skuldsku.testrunner.dbrunner.dbverifier.verifiers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.steria.skuldsku.common.result.Results;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.FieldDifference;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifier;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifierOptions;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeAdditionalInActualResult;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeMatchesResult;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeMissingFromActualResult;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeNotEqualsResult;

public class BestFitDatabaseChangeVerifier implements DatabaseChangeVerifier {

    // Possible optimization:
    //
    //  a: 3, b: 2, c: 3 ...
    //  o - old value, n - new value.
    //  ^- if this.o == this.n: pop this
    //     this.o = this.n
    //     if this.n >= next.o: pop this
    //     if this.n > next.n: next.o = next.n; 
    //     if this.next one's actual value is larger: make n
    
    @Override
    public Results assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, DatabaseChangeVerifierOptions databaseChangeVerifierOptions) {
        final Results databaseChangeVerifierResult = new Results();
        
        /*
         * Sort list so that expected data with best fit (ie the most number of fields match a given
         * actual data row) gets treated first.
         */
        List<DatabaseChange> expectedSorted = createSortedListOnBestFit(expected, actual, databaseChangeVerifierOptions.getSkipFields());
        
        /*
         * Connect each expected data row to the actual data rows with the best fit. Note that already
         * matched data cannot be used.
         */
        final Set<DatabaseChange> candidates = new HashSet<DatabaseChange>(actual);
        while (!expectedSorted.isEmpty()) {
            final DatabaseChange expectedDatabaseChange = expectedSorted.remove(0);
            final DatabaseChange actualDatabaseChange = determineBestFitFor(expectedDatabaseChange, candidates, databaseChangeVerifierOptions.getSkipFields());
            if (actualDatabaseChange == null) {
                databaseChangeVerifierResult.addResult(new DatabaseChangeMissingFromActualResult(expectedDatabaseChange));
            } else {
                candidates.remove(actualDatabaseChange);
                final List<FieldDifference> fieldDifferences = expectedDatabaseChange.determineDifferences(actualDatabaseChange, databaseChangeVerifierOptions.getSkipFields());
                if (!fieldDifferences.isEmpty()) {
                    databaseChangeVerifierResult.addResult(new DatabaseChangeNotEqualsResult(expectedDatabaseChange, actualDatabaseChange, fieldDifferences));
                } else if (databaseChangeVerifierOptions.isIncludeSuccessfulMatchesInResult()) {
                    databaseChangeVerifierResult.addResult(new DatabaseChangeMatchesResult(expectedDatabaseChange, actualDatabaseChange));
                }
            }
            
            expectedSorted = createSortedListOnBestFit(expectedSorted, candidates, databaseChangeVerifierOptions.getSkipFields());
        }
        for (DatabaseChange unmatchedCandidate : candidates) {
            databaseChangeVerifierResult.addResult(new DatabaseChangeAdditionalInActualResult(unmatchedCandidate));
        }
        
        return databaseChangeVerifierResult;
    }

    private static List<DatabaseChange> createSortedListOnBestFit(final Collection<DatabaseChange> expected,
            final Collection<DatabaseChange> actual, final Set<String> skipFields) {
        final List<DatabaseChange> expectedSorted = new ArrayList<DatabaseChange>(expected);
        Collections.sort(expectedSorted, new Comparator<DatabaseChange>() {
            @Override
            public int compare(DatabaseChange dc1, DatabaseChange dc2) {
                final DatabaseChange bestFitForDc1 = determineBestFitFor(dc1, actual, skipFields);
                final int fieldsMatchDc1 = (bestFitForDc1 != null) ? dc1.fieldsMatched(bestFitForDc1, skipFields) : 0;
                
                final DatabaseChange bestFitForDc2 = determineBestFitFor(dc2, actual, skipFields);
                final int fieldsMatchDc2 = (bestFitForDc2 != null) ? dc2.fieldsMatched(bestFitForDc2, skipFields) : 0;
                
                return Integer.compare(fieldsMatchDc2, fieldsMatchDc1);
            }
        });
        return expectedSorted;
    }
    
    private static DatabaseChange determineBestFitFor(DatabaseChange d, Collection<DatabaseChange> candidates, Set<String> skipFields) {
        DatabaseChange bestFit = null;
        int bestFieldsMatched = -1;
        
        for (DatabaseChange candidate : candidates) {
            final int fieldsMatched = d.fieldsMatched(candidate, skipFields);
            if (fieldsMatched > bestFieldsMatched) {
                bestFit = candidate;
                bestFieldsMatched = fieldsMatched;
            }
        }
        return bestFit;
    }
}
