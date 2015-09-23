package no.steria.skuldsku.recorder.http;

import java.io.BufferedReader;
import java.io.IOException;

public class ProxyBufferedReader extends BufferedReader {
    public ProxyBufferedReader(BufferedReader reader, HttpCall httpCall) {
        super(reader);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return super.read(cbuf, off, len);
    }
}
