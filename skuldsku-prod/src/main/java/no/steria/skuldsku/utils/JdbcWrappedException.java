package no.steria.skuldsku.utils;

/**
 * A wrapper class for <code>SQLException</code>.
 * 
 * {@link #getCause()} is guarantied never to return <code>null</code>.
 * 
 * @see java.sql.SQLException
 */
public class JdbcWrappedException extends JdbcException {
    private static final long serialVersionUID = 1L;

    public JdbcWrappedException(String message, Exception rootCause) {
        super(message, rootCause);
        
        if (rootCause == null) {
            throw new NullPointerException("rootCause == null (message="+ message + ")");
        }
    }
    
    public JdbcWrappedException(Exception rootCause) {
        super(rootCause);
        
        if (rootCause == null) {
            throw new NullPointerException("rootCause == null");
        }
    }
    
}
