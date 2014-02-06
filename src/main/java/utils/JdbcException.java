package utils;

public class JdbcException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JdbcException(String message) {
        super(message);
    }
            
    protected JdbcException(String message, Exception rootCause) {
        super(message, rootCause);
    }
    
    protected JdbcException(Exception rootCause) {
        super(rootCause);
    }
}
