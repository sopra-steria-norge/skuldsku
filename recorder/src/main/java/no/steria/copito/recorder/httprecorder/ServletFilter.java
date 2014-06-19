package no.steria.copito.recorder.httprecorder;

import no.steria.copito.recorder.RecorderFacade;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Filter for recording HTTP interactions.
 */
public abstract class ServletFilter implements Filter{


    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(!RecorderFacade.recordingIsOn()){
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest req = (HttpServletRequest) request;

        ReportObject reportObject = new ReportObject();
        reportObject.setMethod(req.getMethod());
        recordPath(req, reportObject);

        RequestWrapper requestSpy = new RequestWrapper(req, reportObject);

        HttpServletResponse resp = (HttpServletResponse) response;

        ResponseWrapper responseSpy = new ResponseWrapper(resp);

        logHeaders(req,reportObject);

        chain.doFilter(requestSpy,responseSpy);

        reportObject.setOutput(responseSpy.getWritten());

        CallReporter reporter = getReporter();
        if (reporter != null) {
            reporter.reportCall(reportObject);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        RecorderFacade.registerFilter(this);
    }

    private void logHeaders(HttpServletRequest req, ReportObject reportObject) {
        Map<String,List<String>> headers = new HashMap<>();

        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = req.getHeaders(headerName);
            List<String> values = new ArrayList<>();
            while (headerValues.hasMoreElements()) {
                values.add(headerValues.nextElement());
            }
            headers.put(headerName,values);
        }
        reportObject.setHeaders(headers);
    }

    private void recordPath(HttpServletRequest req, ReportObject reportObject) {
        StringBuilder path = new StringBuilder(req.getServletPath());
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            path.append(pathInfo);
        }

        reportObject.setPath(path.toString());
    }

    public abstract CallReporter getReporter();



}
