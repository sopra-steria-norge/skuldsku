package no.steria.copito.dbrecorder.dbverifier.verifiers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.steria.copito.dbrecorder.dbchange.DatabaseChange;
import no.steria.copito.dbrecorder.dbverifier.DatabaseChangeVerifier;
import no.steria.copito.dbrecorder.dbverifier.VerifierOptions;
import no.steria.copito.dbrecorder.dbverifier.VerifierResult;

public class SimpleBestFitDatabaseChangeVerifier implements DatabaseChangeVerifier {

    @Override
    public VerifierResult assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, VerifierOptions verifierOptions) {
        final VerifierResult verifierResult = new VerifierResult();
        
        /*
         * Sort list so that expected data with best fit (ie the most number of fields match a given
         * actual data row) gets treated first.
         */
        final List<DatabaseChange> expectedSorted = createSortedListOnBestFit(expected, actual, verifierOptions.getSkipFields());
        
        /*
         * Connect each expected data row to the actual data rows with the best fit. Note that already
         * matched data cannot be used.
         */
        final Set<DatabaseChange> candidates = new HashSet<DatabaseChange>(actual);
        for (DatabaseChange expectedDatabaseChange : expectedSorted) {
            final DatabaseChange actualDatabaseChange = determineBestFitFor(expectedDatabaseChange, candidates, verifierOptions.getSkipFields());
            if (actualDatabaseChange == null) {
                verifierResult.addMissingFromActual(expectedDatabaseChange);
            } else {
                candidates.remove(actualDatabaseChange);
                final boolean match = expectedDatabaseChange.equals(actualDatabaseChange, verifierOptions.getSkipFields());
                if (!match) {
                    verifierResult.addNotEquals(expectedDatabaseChange, actualDatabaseChange);
                }
            }
        }
        for (DatabaseChange unmatchedCandidate : candidates) {
            verifierResult.addAdditionalInActual(unmatchedCandidate);
        }
        
        return verifierResult;
    }

    private static List<DatabaseChange> createSortedListOnBestFit(final List<DatabaseChange> expected,
            final List<DatabaseChange> actual, final Set<String> skipFields) {
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
