package no.steria.skuldsku.recorder;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import no.steria.skuldsku.recorder.dbrecorder.DatabaseRecorder;
import no.steria.skuldsku.recorder.dbrecorder.impl.oracle.OracleDatabaseRecorder;
import no.steria.skuldsku.recorder.httprecorder.CallReporter;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.AsyncMode;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.InterfaceRecorderConfig;
import no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder.ReportCallback;

public final class SkuldskuConfig {

    private final List<DatabaseRecorder> databaseRecorders = new ArrayList<DatabaseRecorder>();
    
    private ReportCallback reportCallback = null; // TODO: Default value
    private CallReporter callReporter = null;
    
    public SkuldskuConfig() {
        
    }
    
    SkuldskuConfig(SkuldskuConfig config) {
        this.databaseRecorders.addAll(config.databaseRecorders);
        this.reportCallback = config.reportCallback;
        this.callReporter = config.callReporter;
    }
    
    
    public void setReportCallback(ReportCallback reportCallback) {
        this.reportCallback = reportCallback;
    }
    
    ReportCallback getReportCallback() {
        return reportCallback;
    }
    
    public void setCallReporter(CallReporter callReporter) {
        this.callReporter = callReporter;
    }
    
    public CallReporter getCallReporter() {
        return callReporter;
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
