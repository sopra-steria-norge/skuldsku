package no.steria.httpspy;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestWrapper extends HttpServletRequestWrapper {
    private Map<String,String> parameters;

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
    public BufferedReader getReader() throws IOException {
        return new ProxyBufferedReader(super.getReader(),reportObject);
    }

    private synchronized void readParams() {
        if (parameters != null
    }

    @Override
    public String getParameter(String name) {
        try {
            System.out.println(toString(getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String parameter = super.getParameter(name);
        reportObject.getParametersRead().put(name,parameter);
        return parameter;
    }

    private static String toString(InputStream inputStream) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            StringBuilder result = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                result.append((char)c);
            }
            return result.toString();
        }
    }



}
