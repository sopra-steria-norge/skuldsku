package utils;

public class JdbcException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JdbcException(String message) {
        super(message);
    }
            
    public JdbcException(String message, Exception rootCause) {
        super(message, rootCause);
    }
    
    public JdbcException(Exception rootCause) {
        super(rootCause);
    }
}
