package no.steria.httpspy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ProxyBufferedReader extends BufferedReader {
    public ProxyBufferedReader(BufferedReader reader, ReportObject reportObject) {
        super(reader);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return super.read(cbuf, off, len);
    }
}
