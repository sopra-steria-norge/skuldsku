package no.steria.skuldsku.testrunner.dbrunner.dbverifier.result;

import java.util.List;
import java.util.Map;

import no.steria.skuldsku.common.result.ComparisionResult;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.FieldDifference;

public class DatabaseChangeNotEqualsResult implements ComparisionResult<DatabaseChange> {
    
    private final DatabaseChange expected;
    private final DatabaseChange actual;
    private final List<FieldDifference> fieldDifferences;
    
    public DatabaseChangeNotEqualsResult(DatabaseChange expected, DatabaseChange actual, List<FieldDifference> fieldDifferences) {
        this.expected = expected;
        this.actual = actual;
        this.fieldDifferences = fieldDifferences;
    }

    @Override
    public String getRequestId() {
        return actual.getClientIdentifier();
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public String getExplanation() {
        return "Actual result (line number "
                + actual.getLineNumber()
                + ") does not match expected (line number "
                + expected.getLineNumber()
                + "). Non-ignored differing values in fields:"
                + nonMatchingFields();
    }
    
    @Override
    public DatabaseChange getExpected() {
        return expected;
    }

    @Override
    public DatabaseChange getActual() {
        return actual;
    }
    
    public List<FieldDifference> getFieldDifferences() {
        return fieldDifferences;
    }
    
    @Override
    public String toString() {
        return getExplanation() + "\nExpected:\n" + expected + "\nGot:\n" + actual + "\n\nField differences:\n" + differences();
    }
    
    private String nonMatchingFields() {
        StringBuilder sb = new StringBuilder();
        for (FieldDifference fd : fieldDifferences) {
            sb.append(" " + fd.getName());
        }
        return sb.toString();
    }
    
    private String differences() {
        StringBuilder sb = new StringBuilder();
        for (FieldDifference fd : fieldDifferences) {
            sb.append(fd.getName() + " expected: " + fd.getExpectedValue() + "\n");
            sb.append(fd.getName() + " actual: " + fd.getActualValue() + "\n\n");
        }
        return sb.toString();
    }
}
