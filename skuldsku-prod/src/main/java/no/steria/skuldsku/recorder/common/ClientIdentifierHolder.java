package no.steria.skuldsku.recorder.common;

public class ClientIdentifierHolder {

    private static ThreadLocal<String> clientIdentifier = new ThreadLocal<String>();
    
    public static void setClientIdentifier(String clientIdentifier) {
        ClientIdentifierHolder.clientIdentifier.set(clientIdentifier);
    }
    
    public static void removeClientIdentifier() {
        clientIdentifier.remove();
    }
    
    public static String getClientIdentifier() {
        return clientIdentifier.get();
    }
}
