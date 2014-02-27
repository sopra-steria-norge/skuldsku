package no.steria.httpspy;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class RequestWrapper extends HttpServletRequestWrapper {
    private CallReporter callReporter;

    public RequestWrapper(HttpServletRequest request,CallReporter callReporter) {
        super(request);
        this.callReporter = callReporter;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ServletInputStream servletIS = super.getInputStream();

        return new ProxyServetInputStream(servletIS,callReporter);


    }
}
