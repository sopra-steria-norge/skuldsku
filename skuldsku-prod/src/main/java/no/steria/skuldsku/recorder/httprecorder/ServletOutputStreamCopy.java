package no.steria.skuldsku.recorder.httprecorder;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ServletOutputStreamCopy extends ServletOutputStream {

    private OutputStream outputStream;
    private ByteArrayOutputStream bys = new ByteArrayOutputStream();

    public ServletOutputStreamCopy(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        bys.write(b);
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
        bys.close();
    }

    public String written() {
        return bys.toString();
    }
}
