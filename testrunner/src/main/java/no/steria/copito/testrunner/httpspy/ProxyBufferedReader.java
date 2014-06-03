package no.steria.copito.testrunner.httpspy;

import java.io.BufferedReader;
import java.io.IOException;

public class ProxyBufferedReader extends BufferedReader {
    public ProxyBufferedReader(BufferedReader reader, ReportObject reportObject) {
        super(reader);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return super.read(cbuf, off, len);
    }
}
