package no.steria.skuldsku.testrunner.dbrunner.dbverifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class DatabaseChangeVerifierOptions {

    private final Set<String> skipFields;
    
    public DatabaseChangeVerifierOptions() {
        this.skipFields = new HashSet<String>(getDefaultSkipFields());
    }
    
    public DatabaseChangeVerifierOptions(DatabaseChangeVerifierOptions databaseChangeVerifierOptions) {
        this.skipFields = new HashSet<String>(databaseChangeVerifierOptions.getSkipFields());
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
