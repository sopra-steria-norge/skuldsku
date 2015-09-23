package no.steria.skuldsku.recorder;

import no.steria.skuldsku.recorder.db.DatabaseRecorder;
import no.steria.skuldsku.recorder.db.impl.oracle.OracleDatabaseRecorder;
import no.steria.skuldsku.recorder.http.HttpCallPersister;
import no.steria.skuldsku.recorder.java.recorder.AsyncMode;
import no.steria.skuldsku.recorder.java.recorder.JavaCallPersister;
import no.steria.skuldsku.recorder.java.recorder.JavaCallRecorderConfig;
import no.steria.skuldsku.recorder.recorders.AbstractRecorderCommunicator;
import no.steria.skuldsku.recorder.recorders.DatabaseRecorderCommunicator;
import no.steria.skuldsku.recorder.recorders.FileRecorderCommunicator;

import javax.sql.DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class SkuldskuConfig {

    private final List<DatabaseRecorder> databaseRecorders = new ArrayList<>();
    
    private JavaCallPersister javaCallPersister = null; // TODO: Default value
    private HttpCallPersister httpCallPersister = null;

    public SkuldskuConfig() {

    }

    public SkuldskuConfig(String outputFile) {
        try {
            final FileRecorderCommunicator frc = new FileRecorderCommunicator(outputFile);
            javaCallPersister = frc;
            httpCallPersister = frc;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SkuldskuConfig(DataSource dataSource) {
        final AbstractRecorderCommunicator arc = new DatabaseRecorderCommunicator(dataSource);
        javaCallPersister = arc;
        httpCallPersister = arc;
    }

    SkuldskuConfig(SkuldskuConfig config) {
        this.databaseRecorders.addAll(config.databaseRecorders);
        this.javaCallPersister = config.javaCallPersister;
        this.httpCallPersister = config.httpCallPersister;
    }

    public void setJavaIntefaceCallPersister(JavaCallPersister javaCallPersister) {
        this.javaCallPersister = javaCallPersister;
    }

    public JavaCallPersister getJavaIntefaceCallPersister() {
        return javaCallPersister;
    }

    public void setHttpCallPersister(HttpCallPersister httpCallPersister) {
        this.httpCallPersister = httpCallPersister;
    }

    public HttpCallPersister getHttpCallPersister() {
        return httpCallPersister;
    }

    public JavaCallRecorderConfig getJavaCallRecorderConfig() {
        return JavaCallRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create(); // TODO.
    }

    public List<DatabaseRecorder> getDatabaseRecorders() {
        return databaseRecorders;
    }

    public void addDatabaseRecorder(DatabaseRecorder databaseRecorder) {
        databaseRecorders.add(databaseRecorder);
    }

    public void addDataSourceForRecording(DataSource dataSource) {
        // TODO: Update with driver detection when we have other implementations:
        final OracleDatabaseRecorder dbr = new OracleDatabaseRecorder(dataSource);
        databaseRecorders.add(dbr);
    }
}
