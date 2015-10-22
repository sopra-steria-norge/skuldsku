package no.steria.skuldsku.recorder.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.xml.bind.DatatypeConverter;

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
        final String result = DatatypeConverter.printBase64Binary(bys.toByteArray());
        return "base64:" + result;
    }
}
