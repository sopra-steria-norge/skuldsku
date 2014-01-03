package runner;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import dbverifier.DatabaseChangeVerifier;
import dbverifier.VerifierOptions;
import dbverifier.verifiers.BestFitDatabaseChangeVerifier;

public class DatabaseRecorderRunnerConfig {

    private DatabaseChangeVerifier databaseChangeVerifier;
    private File baseDirectory;
    private VerifierOptions defaultVerifierOptions;
    private boolean rollbackEnabled;
    
    
    public DatabaseRecorderRunnerConfig() {
        this.defaultVerifierOptions = new VerifierOptions();
        this.baseDirectory = new File("src/test/resources");
        this.databaseChangeVerifier = new BestFitDatabaseChangeVerifier();
        this.rollbackEnabled = true;
    }

    
    public void setDefaultVerifierOptions(VerifierOptions defaultVerifierOptions) {
        this.defaultVerifierOptions = defaultVerifierOptions;
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

    public VerifierOptions getDefaultVerifierOptions() {
        return defaultVerifierOptions;
    }
    
    boolean isRollbackEnabled() {
        return rollbackEnabled;
    }
}
