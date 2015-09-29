package no.steria.skuldsku.recorder.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.steria.skuldsku.recorder.Skuldsku;
import no.steria.skuldsku.recorder.common.ClientIdentifierHolder;
import no.steria.skuldsku.recorder.logging.RecorderLog;

/**
 * Filter for recording HTTP interactions.
 */
public class SkuldskuFilter implements Filter{

    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(!Skuldsku.isRecordingOn()){
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;

        HttpCall httpCall = new HttpCall();
        httpCall.setMethod(req.getMethod());
        recordPath(req, httpCall);

        RequestWrapper requestSpy = new RequestWrapper(req, httpCall);

        HttpServletResponse resp = (HttpServletResponse) response;

        ResponseWrapper responseSpy = new ResponseWrapper(resp);

        logHeaders(req, httpCall);

        final String requestId = UUID.randomUUID().toString();
        ClientIdentifierHolder.setClientIdentifier(requestId);
        httpCall.setClientIdentifier(requestId);
        chain.doFilter(requestSpy,responseSpy);
        //resp.flushBuffer();
        httpCall.setOutput(responseSpy.getWritten());
        httpCall.setStatus(resp.getStatus());
        httpCall.setResponseHeaders(getResponseHeaders(resp));

        HttpCallPersister reporter = getReporter();
        if (reporter != null) {
            reporter.reportCall(httpCall);
        } else {
            RecorderLog.error("There is no CallReporter associated with the current HTTP filter. HTTP interactions will not be recorded.");
        }
        
        ClientIdentifierHolder.removeClientIdentifier();
    }

    private void logHeaders(HttpServletRequest req, HttpCall httpCall) {
        Map<String, List<String>> headers = getRequestHeaders(req);
        httpCall.setHeaders(headers);
    }

    private Map<String, List<String>> getRequestHeaders(HttpServletRequest req) {
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
        return headers;
    }
    
    private Map<String, List<String>> getResponseHeaders(HttpServletResponse response) {
        Map<String,List<String>> headers = new HashMap<>();

        for (String headerName : response.getHeaderNames()) {
            List<String> values = new ArrayList<>(response.getHeaders(headerName));
            headers.put(headerName,values);
        }
        return headers;
    }

    private void recordPath(HttpServletRequest req, HttpCall httpCall) {
        StringBuilder path = new StringBuilder(req.getServletPath());
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            path.append(pathInfo);
        }

        httpCall.setPath(path.toString());
    }

    public HttpCallPersister getReporter() {
        return Skuldsku.getSkuldskuConfig().getHttpCallPersister();
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }
    
    @Override
    public void destroy() {
        
    }
}
