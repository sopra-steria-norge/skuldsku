package no.steria.skuldsku.recorder;

import no.steria.skuldsku.recorder.dbrecorder.DatabaseRecorder;
import no.steria.skuldsku.recorder.dbrecorder.impl.oracle.OracleDatabaseRecorder;
import no.steria.skuldsku.recorder.httprecorder.HttpCallPersister;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.AsyncMode;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.InterfaceRecorderConfig;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaIntefaceCallPersister;
import no.steria.skuldsku.recorder.recorders.AbstractRecorderCommunicator;
import no.steria.skuldsku.recorder.recorders.DatabaseRecorderCommunicator;
import no.steria.skuldsku.recorder.recorders.FileRecorderCommunicator;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class SkuldskuConfig {

    private final List<DatabaseRecorder> databaseRecorders = new ArrayList<>();
    
    private JavaIntefaceCallPersister javaIntefaceCallPersister = null; // TODO: Default value
    private HttpCallPersister httpCallPersister = null;

    public SkuldskuConfig() {

    }

    public SkuldskuConfig(String outputFile) {
        try {
            final FileRecorderCommunicator frc = new FileRecorderCommunicator(outputFile);
            javaIntefaceCallPersister = frc;
            httpCallPersister = frc;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SkuldskuConfig(DataSource dataSource) {
        final AbstractRecorderCommunicator arc = new DatabaseRecorderCommunicator(dataSource);
        javaIntefaceCallPersister = arc;
        httpCallPersister = arc;
    }

    SkuldskuConfig(SkuldskuConfig config) {
        this.databaseRecorders.addAll(config.databaseRecorders);
        this.javaIntefaceCallPersister = config.javaIntefaceCallPersister;
        this.httpCallPersister = config.httpCallPersister;
    }

    public void setJavaIntefaceCallPersister(JavaIntefaceCallPersister javaIntefaceCallPersister) {
        this.javaIntefaceCallPersister = javaIntefaceCallPersister;
    }

    public JavaIntefaceCallPersister getJavaIntefaceCallPersister() {
        return javaIntefaceCallPersister;
    }

    public void setHttpCallPersister(HttpCallPersister httpCallPersister) {
        this.httpCallPersister = httpCallPersister;
    }

    public HttpCallPersister getHttpCallPersister() {
        return httpCallPersister;
    }

    public InterfaceRecorderConfig getInterfaceRecorderConfig() {
        return InterfaceRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create(); // TODO.
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
