package runner;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import dbverifier.DatabaseChangeVerifier;
import dbverifier.verifiers.BestFitDatabaseChangeVerifier;

public class DatabaseRecorderRunnerConfig {

    private DatabaseChangeVerifier databaseChangeVerifier;
    private File baseDirectory;
    private Set<String> skipFields;
    private boolean rollbackEnabled;
    
    
    public DatabaseRecorderRunnerConfig() {
        this.skipFields = Collections.singleton("SESSIONID");
        this.baseDirectory = new File("src/test/resources");
        this.databaseChangeVerifier = new BestFitDatabaseChangeVerifier();
        this.rollbackEnabled = true;
    }
    
    
    public void setSkipFields(Set<String> skipFields) {
        this.skipFields = skipFields;
    }
    
    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }
    
    public void setDatabaseChangeVerifier(DatabaseChangeVerifier databaseChangeVerifier) {
        this.databaseChangeVerifier = databaseChangeVerifier;
    }
    
    public void setRollbackEnabled(boolean rollbackEnabled) {
        this.rollbackEnabled = rollbackEnabled;
    }

    DatabaseChangeVerifier getDatabaseChangeVerifier() {
        return databaseChangeVerifier;
    }

    File getBaseDirectory() {
        return baseDirectory;
    }

    Set<String> getSkipFields() {
        return skipFields;
    }
    
    boolean isRollbackEnabled() {
        return rollbackEnabled;
    }
}
