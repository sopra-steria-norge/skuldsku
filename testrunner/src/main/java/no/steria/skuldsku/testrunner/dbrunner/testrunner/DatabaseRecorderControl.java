package no.steria.skuldsku.testrunner.dbrunner.testrunner;

import no.steria.skuldsku.recorder.dbrecorder.DatabaseRecorder;

public final class DatabaseRecorderControl {
    
    private final DatabaseRecorder databaseRecorder;
    private boolean destroyed = false;

    
    DatabaseRecorderControl(DatabaseRecorder databaseRecorder) {
        this.databaseRecorder = databaseRecorder;
    }
    
    
    public void pause() {
        ensureAlive();
        databaseRecorder.stop();
    }
    
    public void resume() {
        ensureAlive();
        databaseRecorder.start();
    }
    
    
    void ensureAlive() {
        if (destroyed) {
            throw new IllegalStateException("The DatabaseRecorderControl should only be used within the callback. Please use DatabaseRecorder instead if you need fine-grained control over recording.");
        }
    }
    
    void destroy() {
        destroyed = true;
    }
}
