package no.steria.skuldsku.testrunner.dbrunner.testrunner;

import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifier;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifierOptions;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.verifiers.BestFitDatabaseChangeVerifier;

import java.io.File;

public class DatabaseRecorderRunnerConfig {

    private DatabaseChangeVerifier databaseChangeVerifier;
    private File baseDirectory;
    private DatabaseChangeVerifierOptions defaultVerifierOptions;
    private boolean rollbackEnabled;
    private VerifierResultHandler verifierResultHandler;
    
    
    public DatabaseRecorderRunnerConfig() {
        this.defaultVerifierOptions = new DatabaseChangeVerifierOptions();
        this.baseDirectory = new File("src/test/resources");
        this.databaseChangeVerifier = new BestFitDatabaseChangeVerifier();
        this.rollbackEnabled = true;
        this.verifierResultHandler = new VerifierResultHandlerImpl();
    }

    
    public void setVerifierResultHandler(VerifierResultHandler verifierResultHandler) {
        this.verifierResultHandler = verifierResultHandler;
    }
    
    public VerifierResultHandler getVerifierResultHandler() {
        return verifierResultHandler;
    }
    
    public void setDefaultVerifierOptions(DatabaseChangeVerifierOptions defaultVerifierOptions) {
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

    public DatabaseChangeVerifierOptions getDefaultVerifierOptions() {
        return defaultVerifierOptions;
    }
    
    boolean isRollbackEnabled() {
        return rollbackEnabled;
    }
}
