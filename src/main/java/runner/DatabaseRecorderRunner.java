package runner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import junit.framework.Assert;
import dbchange.DatabaseChange;
import dbchange.DatabaseChangeRollback;
import dbrecorder.DatabaseRecorder;
import dbrecorder.impl.oracle.OracleDatabaseRecorder;
import dbverifier.DatabaseChangeVerifier;
import dbverifier.VerifierOptions;
import dbverifier.VerifierResult;
import dbverifier.VerifierResult.VerifierResultPair;

public final class DatabaseRecorderRunner {

    private static final String EXPECTED_DIRECTORY_NAME = "expected";
    private static final String ACTUAL_DIRECTORY_NAME = "actual";
    
    private final DatabaseRecorder databaseRecorder;
    private final DatabaseChangeRollback databaseChangeRollback;
    private final DatabaseChangeVerifier databaseChangeVerifier;
    private final File baseDirectory;
    private final VerifierOptions defaultVerifierOptions;
    private final boolean rollbackEnabled;
    

    public DatabaseRecorderRunner(DataSource dataSource) {
        this(dataSource, new DatabaseRecorderRunnerConfig());
    }
    
    public DatabaseRecorderRunner(DataSource dataSource, DatabaseRecorderRunnerConfig config) {
        this(new OracleDatabaseRecorder(dataSource),
                new DatabaseChangeRollback(dataSource),
                config);
    }
    
    DatabaseRecorderRunner(DatabaseRecorder databaseRecorder, DatabaseChangeRollback databaseChangeRollback, DatabaseRecorderRunnerConfig config) {
        if (!config.getBaseDirectory().exists()) {
            throw new IllegalArgumentException("Given directory does not exist: " + config.getBaseDirectory().getAbsolutePath());
        }
        if (!config.getBaseDirectory().isDirectory()) {
            throw new IllegalArgumentException("Given file is not a directory: " + config.getBaseDirectory().getAbsolutePath());
        }
        
        this.databaseRecorder = databaseRecorder;
        this.databaseChangeRollback = databaseChangeRollback;
        this.databaseChangeVerifier = config.getDatabaseChangeVerifier();
        this.baseDirectory = config.getBaseDirectory();
        this.defaultVerifierOptions = config.getDefaultVerifierOptions();
        this.rollbackEnabled = config.isRollbackEnabled();
    }
    
    
    public void recordAndCompare(DatabaseRecorderCallback callback, String filename) {
        recordAndCompare(callback, filename, defaultVerifierOptions);
    }
    
    public void recordAndCompare(DatabaseRecorderCallback callback, String filename, VerifierOptions verifierOptions) {
        final File actualDirectory = createDirectoryIfNotExists(baseDirectory, ACTUAL_DIRECTORY_NAME);
        final File actualFile = new File(actualDirectory, filename);
        
        try {
            record(actualFile, callback);
        } finally {
            if (rollbackEnabled) {
                databaseChangeRollback.rollback(actualFile);
            }
        }
        
        final File expectedDirectory = createDirectoryIfNotExists(baseDirectory, EXPECTED_DIRECTORY_NAME);
        final File expectedFile = new File(expectedDirectory, filename);
        
        assertEquals(actualFile, expectedFile, verifierOptions);
    }

    private void assertEquals(final File actualFile, final File expectedFile, VerifierOptions verifierOptions) {
        if (expectedFile.exists()) {
            final VerifierResult result = databaseChangeVerifier.assertEquals(
                    DatabaseChange.readDatabaseChanges(expectedFile),
                    DatabaseChange.readDatabaseChanges(actualFile),
                    verifierOptions);
            // TODO: JUnit-mapper (so that other test suites than JUnit can be supported)
            for (DatabaseChange expectedDatabaseChange : result.getMissingFromActual()) {
                Assert.fail("Cannot find actual data matching expected data on line: " + expectedDatabaseChange.getLineNumber());
            }
            for (VerifierResultPair pair : result.getNotEquals()) {
                Assert.fail("The actual data on line " + pair.getActual().getLineNumber()
                        + " does not match the expected data on line number " + pair.getExpected().getLineNumber());
            }
            for (DatabaseChange unmatchedCandidate : result.getAdditionalInActual()) {
                Assert.fail("Could not find expected data matching actual data on line " + unmatchedCandidate.getLineNumber());
            }
        } else {
            Assert.fail("The file that should be containing the expected data does not exist: " + expectedFile.getAbsolutePath()
                    + "\nIf this is the first execution of the test, you can take a look at the actual data ("
                    + actualFile.getAbsolutePath()
                    + ") and check if the result is correct. If correct, you can use this file as the expected data.");
        }
    }
    
    private File createDirectoryIfNotExists(File baseDirectory, String directoryName) {       
        final File actualDirectory = new File(baseDirectory, directoryName);
        actualDirectory.mkdirs();
        return actualDirectory;
    }
    
    private void record(File recordingFile, DatabaseRecorderCallback callback) {
        try {
            final PrintWriter out = new PrintWriter(recordingFile);
            try {
                record(out, callback);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void record(PrintWriter out, DatabaseRecorderCallback callback) {
        final DatabaseRecorderControl control = new DatabaseRecorderControl(databaseRecorder);
        
        databaseRecorder.tearDown();
        databaseRecorder.setup();
        databaseRecorder.start();
        try {
            callback.execute(control);
        } finally {
            databaseRecorder.stop();
            databaseRecorder.exportTo(out);
            databaseRecorder.tearDown();
        }
        
        control.destroy();
    }
    
    private static <T> Set<T> alwaysHashSetAndNeverNull(Set<T> set) {
        return (set != null) ? new HashSet<T>(set) : Collections.<T>emptySet();
    }
}
