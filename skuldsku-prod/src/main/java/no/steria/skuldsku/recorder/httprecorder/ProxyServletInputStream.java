package no.steria.skuldsku.recorder.httprecorder;

import javax.servlet.ServletInputStream;
import java.io.IOException;

public class ProxyServletInputStream extends ServletInputStream {

    private final ServletInputStream delegate;
    private final HttpCall httpCall;
    private StringBuilder result = new StringBuilder();

    public ProxyServletInputStream(ServletInputStream delegate, HttpCall httpCall) {
        this.delegate = delegate;
        this.httpCall = httpCall;
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
            httpCall.setReadInputStream(result.toString());
            result = null;
        }
        delegate.close();
    }

}
