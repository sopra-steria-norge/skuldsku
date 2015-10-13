package no.steria.skuldsku.testrunner.dbrunner.dbverifier.result;

import no.steria.skuldsku.common.result.ComparisionResult;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;

public class DatabaseChangeMatchesResult implements ComparisionResult<DatabaseChange> {
    
    private final DatabaseChange expected;
    private final DatabaseChange actual;
    
    public DatabaseChangeMatchesResult(DatabaseChange expected, DatabaseChange actual) {
        this.expected = expected;
        this.actual = actual;
    }

    @Override
    public String getStartTime() {
        return actual.getStartTime();
    }
    
    @Override
    public String getRequestId() {
        return actual.getClientIdentifier();
    }
    
    @Override
    public String getTitle() {
        return "Database " + getActual().getTableName() + " " + getActual().getAction() + " matches";
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public String getExplanation() {
        return "Actual result (line number "
                + actual.getLineNumber()
                + ") matches expected (line number "
                + expected.getLineNumber()
                + ").";
    }
    
    @Override
    public DatabaseChange getExpected() {
        return expected;
    }

    @Override
    public DatabaseChange getActual() {
        return actual;
    }
    
    @Override
    public String toString() {
        return getExplanation();
    }
}
