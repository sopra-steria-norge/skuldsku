package no.steria.httpspy;

import javax.servlet.ServletInputStream;
import java.io.IOException;

public class ProxyServetInputStream extends ServletInputStream {

    private ServletInputStream delegate;
    private CallReporter callReporter;
    private StringBuilder result = new StringBuilder();

    public ProxyServetInputStream(ServletInputStream delegate, CallReporter callReporter) {
        this.delegate = delegate;
        this.callReporter = callReporter;
    }

    @Override
    public int read() throws IOException {
        int readb = delegate.read();
        if (readb != -1) {
            char c = (char) readb;
            result.append(c);
        }
        return readb;
    }

    @Override
    public void close() throws IOException {
        if (result != null) {
            callReporter.reportCall(result.toString());
            result = null;
        }
        delegate.close();
    }

}
