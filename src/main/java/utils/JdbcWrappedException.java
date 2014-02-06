package utils;

public class JdbcWrappedException extends JdbcException {
    private static final long serialVersionUID = 1L;

    public JdbcWrappedException(String message, Exception rootCause) {
        super(message, rootCause);
    }
    
    public JdbcWrappedException(Exception rootCause) {
        super(rootCause);
    }
    
}
