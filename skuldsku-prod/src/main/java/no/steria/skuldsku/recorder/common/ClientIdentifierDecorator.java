package no.steria.skuldsku.recorder.common;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class ClientIdentifierDecorator implements DataSource {

    private final DataSource dataSource;
    private final ClientIdentifierCallback callback;
    
    
    public ClientIdentifierDecorator(DataSource dataSource, ClientIdentifierCallback callback) {
        this.dataSource = dataSource;
        this.callback = callback;
    }
    

    public Connection getConnection() throws SQLException {
        final Connection c = dataSource.getConnection();
        //executeSqlOn(c);
        return new ClientIdentifierConnection(c, callback);
    }
    
    public Connection getConnection(String username, String password) throws SQLException {
        final Connection c = dataSource.getConnection(username, password);
        //executeSqlOn(c);
        return new ClientIdentifierConnection(c, callback);
    }

    /*
    private void executeSqlOn(final Connection c) throws SQLException {
        final String clientIdentifier = callback.getClientIdentifier();
        if (clientIdentifier == null) {
            return;
        }
        
        final CallableStatement s = c.prepareCall("begin dbms_session.set_identifier(?); end;");
        try {
            s.setString(1, clientIdentifier);
            s.execute();
        } finally {
            try {
                s.close();
            } catch (SQLException e) {}
        }
        c.commit();
    }
    */

    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }
    
    
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == null) {
            return false;
        }
        if (iface.isAssignableFrom(dataSource.getClass())) {
            return true;
        }
        return dataSource.isWrapperFor(iface);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(dataSource.getClass())) {
            return (T) dataSource;
        }
        return dataSource.unwrap(iface);
    }
}
