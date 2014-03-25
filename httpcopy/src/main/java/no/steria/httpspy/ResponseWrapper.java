package no.steria.httpspy;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ResponseWrapper extends HttpServletResponseWrapper {
    private StringWriter copier;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        PrintWriter writer = super.getWriter();
        copier = new StringWriter();
        return new PrintWriterCopier(copier,writer);
    }

    public String getWritten() {
        if (copier == null) {
            return null;
        }
        return copier.toString();
    }
}
