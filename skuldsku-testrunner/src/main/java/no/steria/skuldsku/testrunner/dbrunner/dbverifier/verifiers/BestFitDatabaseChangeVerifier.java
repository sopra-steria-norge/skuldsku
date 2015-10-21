package no.steria.skuldsku.testrunner.dbrunner.dbverifier.verifiers;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import no.steria.skuldsku.common.result.Results;
import no.steria.skuldsku.testrunner.common.ClientIdentifierMapper;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.FieldDifference;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifier;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifierOptions;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeAdditionalInActualResult;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeMatchesResult;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeMissingFromActualResult;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeNotEqualsResult;

public class BestFitDatabaseChangeVerifier implements DatabaseChangeVerifier {

    @Override
    public Results assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, DatabaseChangeVerifierOptions databaseChangeVerifierOptions) {
        final Results databaseChangeVerifierResult = new Results();
        
        final Set<DatabaseChange> expectedSet = new LinkedHashSet<DatabaseChange>(expected);
        final Set<DatabaseChange> actualSet = new LinkedHashSet<DatabaseChange>(actual);
        
        consumeSuccessfulMatches(expectedSet, actualSet, databaseChangeVerifierOptions, databaseChangeVerifierResult);
        consumeNotEqualMatches(expectedSet, actualSet, databaseChangeVerifierOptions, databaseChangeVerifierResult);
        
        for (DatabaseChange d : actualSet) {
            databaseChangeVerifierResult.addResult(new DatabaseChangeAdditionalInActualResult(d));
        }
        
        for (DatabaseChange d : expectedSet) {
            final String clientIdentifier = determineClientIdentifier(d, databaseChangeVerifierOptions);
            databaseChangeVerifierResult.addResult(new DatabaseChangeMissingFromActualResult(d, clientIdentifier));
        }
        
        return databaseChangeVerifierResult;
    }

    private String determineClientIdentifier(DatabaseChange d, DatabaseChangeVerifierOptions databaseChangeVerifierOptions) {
        if (databaseChangeVerifierOptions.getClientIdentifierMapper() == null) {
            return "";
        }
        final String s = databaseChangeVerifierOptions.getClientIdentifierMapper().translateToActual(d.getClientIdentifier());
        return (s != null) ? s : "";
    }

    private void consumeSuccessfulMatches(final Set<DatabaseChange> expectedSet,
            final Set<DatabaseChange> actualSet,
            final DatabaseChangeVerifierOptions databaseChangeVerifierOptions,
            final Results databaseChangeVerifierResult) {
        final Iterator<DatabaseChange> actualSetIterator = actualSet.iterator();
        while (actualSetIterator.hasNext()) {
            final DatabaseChange actualDatabaseChange = actualSetIterator.next();
            
            final DatabaseChange expectedDatabaseChange = findPerfectMatchInExpectedAndRemove(actualDatabaseChange, expectedSet, databaseChangeVerifierOptions);
            if (expectedDatabaseChange != null) {
                actualSetIterator.remove();
                if (databaseChangeVerifierOptions.isIncludeSuccessfulMatchesInResult()) {
                    databaseChangeVerifierResult.addResult(new DatabaseChangeMatchesResult(expectedDatabaseChange, actualDatabaseChange));
                }
            }
        }
    }
    
    private void consumeNotEqualMatches(final Set<DatabaseChange> expectedSet,
            final Set<DatabaseChange> actualSet,
            DatabaseChangeVerifierOptions databaseChangeVerifierOptions,
            final Results databaseChangeVerifierResult) {
        final Iterator<DatabaseChange> actualSetIterator = actualSet.iterator();
        while (actualSetIterator.hasNext()) {
            final DatabaseChange actualDatabaseChange = actualSetIterator.next();
            
            final DatabaseChange expectedDatabaseChange = findClosestMatchInExptectedAndRemove(actualDatabaseChange, expectedSet, databaseChangeVerifierOptions);
            if (expectedDatabaseChange != null) {
                actualSetIterator.remove();
                final List<FieldDifference> fieldDifferences = expectedDatabaseChange.determineDifferences(actualDatabaseChange, databaseChangeVerifierOptions.getSkipFields());
                databaseChangeVerifierResult.addResult(new DatabaseChangeNotEqualsResult(expectedDatabaseChange, actualDatabaseChange, fieldDifferences));
            }
        }
    }

    private DatabaseChange findPerfectMatchInExpectedAndRemove(final DatabaseChange actualDatabaseChange, final Set<DatabaseChange> expectedCandidates,
            DatabaseChangeVerifierOptions databaseChangeVerifierOptions) {
        final Iterator<DatabaseChange> candidateIterator = expectedCandidates.iterator();
        while (candidateIterator.hasNext()) {
            final DatabaseChange candidate = candidateIterator.next();
            
            if (!isSameClientIdentifier(candidate, actualDatabaseChange, databaseChangeVerifierOptions)) {
                continue;
            } 
            
            final List<FieldDifference> fieldDifferences = candidate.determineDifferences(actualDatabaseChange, databaseChangeVerifierOptions.getSkipFields());
            if (fieldDifferences.isEmpty()) {
                candidateIterator.remove();
                return candidate;
            }
        }
        return null;
    }

    private boolean isSameClientIdentifier(final DatabaseChange expectedDatabaseChange, final DatabaseChange actualDatabaseChange,
            DatabaseChangeVerifierOptions databaseChangeVerifierOptions) {
        if (databaseChangeVerifierOptions.getClientIdentifierMapper() == null) {
            return true;
        }
        if (actualDatabaseChange.getClientIdentifier() == null) {
            return true;
        }
        if (expectedDatabaseChange.getClientIdentifier() == null) {
            return true;
        }
        
        final ClientIdentifierMapper m = databaseChangeVerifierOptions.getClientIdentifierMapper();
        final String mappedClientIdentifier = m.translateToActual(expectedDatabaseChange.getClientIdentifier());
        
        return mappedClientIdentifier.equals(actualDatabaseChange.getClientIdentifier());
    }
    
    private DatabaseChange findClosestMatchInExptectedAndRemove(final DatabaseChange actualDatabaseChange, final Set<DatabaseChange> exptectedCandidates,
            DatabaseChangeVerifierOptions databaseChangeVerifierOptions) {
        int bestFieldDifferences = Integer.MAX_VALUE;
        DatabaseChange bestDatabaseChange = null;
        
        final Iterator<DatabaseChange> candidateIterator = exptectedCandidates.iterator();
        while (candidateIterator.hasNext()) {
            final DatabaseChange candidate = candidateIterator.next();
            
            if (!isSameClientIdentifier(candidate, actualDatabaseChange, databaseChangeVerifierOptions)) {
                continue;
            } 
            
            if (actualDatabaseChange.getTableName() != null
                    && candidate.getTableName() != null
                    && !actualDatabaseChange.getTableName().equals(candidate.getTableName())) {
                continue;
            }
            
            final List<FieldDifference> fieldDifferences = candidate.determineDifferences(actualDatabaseChange, databaseChangeVerifierOptions.getSkipFields());
            if (fieldDifferences.size() < bestFieldDifferences) {
                bestFieldDifferences = fieldDifferences.size();
                bestDatabaseChange = candidate;
            }
        }
        if (bestDatabaseChange != null) {
            exptectedCandidates.remove(bestDatabaseChange);
        }
        return bestDatabaseChange;
    }
}
