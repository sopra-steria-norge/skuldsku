package no.steria.skuldsku.testrunner.dbrunner.dbverifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class DatabaseVerifierOptions {

    private final Set<String> skipFields;
    
    public DatabaseVerifierOptions() {
        this.skipFields = new HashSet<String>(getDefaultSkipFields());
    }
    
    public DatabaseVerifierOptions(DatabaseVerifierOptions databaseVerifierOptions) {
        this.skipFields = new HashSet<String>(databaseVerifierOptions.getSkipFields());
    }
    
    
    public void addSkipField(String fieldName) {
        skipFields.add(fieldName);
    }
    
    private static Set<String> getDefaultSkipFields() {
        return Collections.singleton("SESSIONID");
    }
    
    public Set<String> getSkipFields() {
        return skipFields;
    }
}
