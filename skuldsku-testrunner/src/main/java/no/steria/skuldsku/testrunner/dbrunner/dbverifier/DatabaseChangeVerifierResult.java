package no.steria.skuldsku.testrunner.dbrunner.dbverifier;

import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DatabaseChangeVerifierResult {

    private final List<DatabaseChange> missingFromActual = new ArrayList<DatabaseChange>();
    private final List<DatabaseChange> additionalInActual = new ArrayList<DatabaseChange>();
    private final List<VerifierResultPair> notEquals = new ArrayList<VerifierResultPair>();
    private final List<String> assertionFailed = new ArrayList<String>();

    public DatabaseChangeVerifierResult() {

    }

    public DatabaseChangeVerifierResult(Collection<DatabaseChangeVerifierResult> results) {
        for (DatabaseChangeVerifierResult vr : results) {
            missingFromActual.addAll(vr.missingFromActual);
            additionalInActual.addAll(vr.additionalInActual);
            notEquals.addAll(vr.notEquals);
            assertionFailed.addAll(vr.assertionFailed);
        }
    }

    public void addMissingFromActual(DatabaseChange databaseChange) {
        missingFromActual.add(databaseChange);
    }

    public List<DatabaseChange> getMissingFromActual() {
        return missingFromActual;
    }

    public void addAdditionalInActual(DatabaseChange databaseChange) {
        additionalInActual.add(databaseChange);
    }

    public List<DatabaseChange> getAdditionalInActual() {
        return additionalInActual;
    }

    public void addNotEquals(DatabaseChange expected, DatabaseChange actual) {
        notEquals.add(new VerifierResultPair(expected, actual));
    }

    public List<VerifierResultPair> getNotEquals() {
        return notEquals;
    }

    public void addAssertionFailed(String explanation) {
        assertionFailed.add(explanation);
    }

    public List<String> getAssertionFailed() {
        return assertionFailed;
    }

    public boolean hasErrors() {
        return !missingFromActual.isEmpty() || !additionalInActual.isEmpty() || !notEquals.isEmpty() || !assertionFailed.isEmpty();
    }

    public static class VerifierResultPair {
        private final DatabaseChange expected;
        private final DatabaseChange actual;

        public VerifierResultPair(DatabaseChange expected, DatabaseChange actual) {
            this.expected = expected;
            this.actual = actual;
        }

        public DatabaseChange getExpected() {
            return expected;
        }

        public DatabaseChange getActual() {
            return actual;
        }
    }
}
