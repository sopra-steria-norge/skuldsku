package no.steria.copito.testrunner.dbrunner.dbverifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class VerifierOptions {

    private final Set<String> skipFields;
    
    public VerifierOptions() {
        this.skipFields = new HashSet<String>(getDefaultSkipFields());
    }
    
    public VerifierOptions(VerifierOptions verifierOptions) {
        this.skipFields = new HashSet<String>(verifierOptions.getSkipFields());
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
