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
    public int readLine(byte[] b, int off, int len) throws IOException {
        return delegate.readLine(b, off, len);
    }

    @Override
    public int read() throws IOException {
        int readb = delegate.read();
        char c = (char) readb;
        result.append(c);
        return readb;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return delegate.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return delegate.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return delegate.skip(n);
    }

    @Override
    public int available() throws IOException {
        return delegate.available();
    }

    @Override
    public void close() throws IOException {
        callReporter.reportCall(result.toString());
        delegate.close();
    }

    @Override
    public void mark(int readlimit) {
        delegate.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        delegate.reset();
    }

    @Override
    public boolean markSupported() {
        return delegate.markSupported();
    }

}
