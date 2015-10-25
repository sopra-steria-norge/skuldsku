package no.steria.skuldsku.testrunner.interfacerunner;

import no.steria.skuldsku.testrunner.common.ClientIdentifierMapper;


public final class JavaCallVerifierOptions {

    private ClientIdentifierMapper clientIdentifierMapper = null;
    
    public JavaCallVerifierOptions() {

    }
    
    public JavaCallVerifierOptions(JavaCallVerifierOptions javaCallVerifierOptions) {
        this.clientIdentifierMapper = javaCallVerifierOptions.clientIdentifierMapper;
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

}
