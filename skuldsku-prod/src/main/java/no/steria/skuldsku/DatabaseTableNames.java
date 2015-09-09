package no.steria.skuldsku;

public final class DatabaseTableNames {
    
    private DatabaseTableNames() {
        
    }

    public static final String SKULDSKU_DATABASE_TABLE_PREFIX = "SKS_";
    public static final String DATABASE_RECORDINGS_TABLE = SKULDSKU_DATABASE_TABLE_PREFIX + "DATABASE_RECORDINGS";
    public static final String JAVA_INTERFACE_RECORDINGS_TABLE = SKULDSKU_DATABASE_TABLE_PREFIX + "JAVA_INTERFACE_RECORDINGS";
    public static final String HTTP_RECORDINGS_TABLE = SKULDSKU_DATABASE_TABLE_PREFIX + "HTTP_RECORDINGS";
}
