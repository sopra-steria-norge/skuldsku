package no.steria.skuldsku.testrunner.dbrunner.dbverifier.result;

import no.steria.skuldsku.common.result.ItemResult;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;


public class DatabaseChangeMissingFromActualResult implements ItemResult<DatabaseChange> {

    private final DatabaseChange databaseChange;
    private final String clientIdentifier;
    
    
    public DatabaseChangeMissingFromActualResult(DatabaseChange databaseChange, String clientIdentifier) {
        this.databaseChange = databaseChange;
        this.clientIdentifier = clientIdentifier;
    }
    
    
    @Override
    public String getStartTime() {
        return null;
    }
    
    @Override
    public String getRequestId() {
        return clientIdentifier;
    }
    
    @Override
    public String getTitle() {
        return "Database " + getItem().getTableName() + " " + getItem().getAction() + " missing from actual";
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public String getExplanation() {
        return "Row in actual data (line number " + databaseChange.getLineNumber() + ") that was not present in the expected data.";
    }

    @Override
    public DatabaseChange getItem() {
        return databaseChange;
    }
    
    @Override
    public String toString() {
        return getExplanation() + " Actual data:\n" + databaseChange.toString();
    }
}
