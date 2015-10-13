package no.steria.skuldsku.testrunner.dbrunner.dbverifier.result;

import no.steria.skuldsku.common.result.ItemResult;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;


public class DatabaseChangeAdditionalInActualResult implements ItemResult<DatabaseChange> {

    private final DatabaseChange databaseChange;
    
    
    public DatabaseChangeAdditionalInActualResult(DatabaseChange databaseChange) {
        this.databaseChange = databaseChange;
    }
    
    @Override
    public String getStartTime() {
        return databaseChange.getStartTime();
    }
    
    @Override
    public String getRequestId() {
        return databaseChange.getClientIdentifier();
    }
    
    @Override
    public String getTitle() {
        return "Database " + getItem().getTableName() + " " + getItem().getAction() + " additional in actual";
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public String getExplanation() {
        return "Missing result in actual data that was present in the expected data (line number " + databaseChange.getLineNumber() + ").";
    }

    @Override
    public DatabaseChange getItem() {
        return databaseChange;
    }
    
    @Override
    public String toString() {
        return getExplanation() + " Expected data:\n" + databaseChange.toString();
    }
}
