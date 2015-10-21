package no.steria.skuldsku.testrunner.dbrunner.dbverifier;

import java.util.HashSet;
import java.util.Set;

import no.steria.skuldsku.testrunner.common.ClientIdentifierMapper;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeMatchesResult;

public final class DatabaseChangeVerifierOptions {

    private final Set<String> skipFields;
    private boolean includeSuccessfulMatchesInResult = true;
    private ClientIdentifierMapper clientIdentifierMapper = null;
    
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
        final Set<String> result = new HashSet<>();
        result.add("SESSIONID");
        result.add("START_TIME");
        result.add("CLIENT_IDENTIFIER");
        return result;
    }
    
    /**
     * Sets a translator from expected client identifier values to actual and
     * requires client identifiers to be equal (after translation) when comparing.
     * 
     * @param clientIdentifierMapper A translator from expected client identifiers to
     *          actual.
     */
    public void setClientIdentifierMapper(ClientIdentifierMapper clientIdentifierMapper) {
        this.clientIdentifierMapper = clientIdentifierMapper;
    }
    
    public ClientIdentifierMapper getClientIdentifierMapper() {
        return clientIdentifierMapper;
    }
    
    /**
     * Sets if successful matches should be added to the result.
     * 
     * @param includeSuccessfulMatchesInResult If <code>true</code> (default value) then
     *          a {@link DatabaseChangeMatchesResult} is added every time the actual
     *          {@link DatabaseChange} matches the expected <code>DatabaseChange</code>.
     */
    public void setIncludeSuccessfulMatchesInResult(boolean includeSuccessfulMatchesInResult) {
        this.includeSuccessfulMatchesInResult = includeSuccessfulMatchesInResult;
    }
    
    /**
     * @see #setIncludeSuccessfulMatchesInResult(boolean)
     */
    public boolean isIncludeSuccessfulMatchesInResult() {
        return includeSuccessfulMatchesInResult;
    }
    
    public Set<String> getSkipFields() {
        return skipFields;
    }
}
