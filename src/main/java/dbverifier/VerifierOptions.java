package dbverifier;

import java.util.Collections;
import java.util.Set;

public final class VerifierOptions {

    private final Set<String> skipFields;
    
    public VerifierOptions() {
        this.skipFields = getDefaultSkipFields();
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
