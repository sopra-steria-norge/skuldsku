package no.steria.skuldsku.recorder;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import no.steria.skuldsku.recorder.dbrecorder.DatabaseRecorder;
import no.steria.skuldsku.recorder.dbrecorder.impl.oracle.OracleDatabaseRecorder;
import no.steria.skuldsku.recorder.httprecorder.HttpCallPersister;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.AsyncMode;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.InterfaceRecorderConfig;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.JavaIntefaceCallPersister;

public final class SkuldskuConfig {

    private final List<DatabaseRecorder> databaseRecorders = new ArrayList<DatabaseRecorder>();
    
    private JavaIntefaceCallPersister javaIntefaceCallPersister = null; // TODO: Default value
    private HttpCallPersister httpCallPersister = null;
    
    public SkuldskuConfig() {
        
    }
    
    SkuldskuConfig(SkuldskuConfig config) {
        this.databaseRecorders.addAll(config.databaseRecorders);
        this.javaIntefaceCallPersister = config.javaIntefaceCallPersister;
        this.httpCallPersister = config.httpCallPersister;
    }
    
    
    public void setJavaIntefaceCallPersister(JavaIntefaceCallPersister javaIntefaceCallPersister) {
        this.javaIntefaceCallPersister = javaIntefaceCallPersister;
    }
    
    JavaIntefaceCallPersister getJavaIntefaceCallPersister() {
        return javaIntefaceCallPersister;
    }
    
    public void setHttpCallPersister(HttpCallPersister httpCallPersister) {
        this.httpCallPersister = httpCallPersister;
    }
    
    public HttpCallPersister getHttpCallPersister() {
        return httpCallPersister;
    }
    
    
    
    InterfaceRecorderConfig getInterfaceRecorderConfig() {
        return InterfaceRecorderConfig.factory().withAsyncMode(AsyncMode.ALL_SYNC).create(); // TODO.
    }
    
    List<DatabaseRecorder> getDatabaseRecorders() {
        return databaseRecorders;
    }
    
    public void addDataSourceForRecording(DataSource dataSource) {
        // TODO: Update with driver detection when we have other implementations:
        final OracleDatabaseRecorder dbr = new OracleDatabaseRecorder(dataSource);
        databaseRecorders.add(dbr);
    }

}
