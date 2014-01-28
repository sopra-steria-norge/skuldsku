package runner;

import java.io.File;

import dbverifier.DatabaseChangeVerifier;
import dbverifier.VerifierOptions;
import dbverifier.verifiers.BestFitDatabaseChangeVerifier;

public class DatabaseRecorderRunnerConfig {

    private DatabaseChangeVerifier databaseChangeVerifier;
    private File baseDirectory;
    private VerifierOptions defaultVerifierOptions;
    private boolean rollbackEnabled;
    private VerifierResultHandler verifierResultHandler;
    
    
    public DatabaseRecorderRunnerConfig() {
        this.defaultVerifierOptions = new VerifierOptions();
        this.baseDirectory = new File("src/test/resources");
        this.databaseChangeVerifier = new BestFitDatabaseChangeVerifier();
        this.rollbackEnabled = true;
        this.verifierResultHandler = new JUnitVerifierResultHandler();
    }

    
    public void setVerifierResultHandler(VerifierResultHandler verifierResultHandler) {
        this.verifierResultHandler = verifierResultHandler;
    }
    
    public VerifierResultHandler getVerifierResultHandler() {
        return verifierResultHandler;
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
