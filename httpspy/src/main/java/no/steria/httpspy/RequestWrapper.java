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

    private final ReportObject reportObject;

    public RequestWrapper(HttpServletRequest request, ReportObject reportObject) {
        super(request);
        this.reportObject = reportObject;

    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ServletInputStream servletIS = super.getInputStream();

        return new ProxyServetInputStream(servletIS,reportObject);


    }

    @Override
    public String getParameter(String name) {
        String parameter = super.getParameter(name);
        reportObject.getParametersRead().put(name,parameter);
        return parameter;
    }

}
