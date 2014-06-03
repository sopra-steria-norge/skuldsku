package no.steria.copito.testrunner.httpspy;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ResponseWrapper extends HttpServletResponseWrapper {
    private StringWriter copier;
    private ServletOutputStreamCopy servletOutputStreamCopy;

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
        if (copier != null) {
            return copier.toString();
        }
        if (servletOutputStreamCopy != null) {
            return servletOutputStreamCopy.written();
        }
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        ServletOutputStream outputStream = super.getOutputStream();
        servletOutputStreamCopy = new ServletOutputStreamCopy(outputStream);
        return servletOutputStreamCopy;
    }
}
