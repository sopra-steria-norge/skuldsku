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
        
        /*
         * 1. Ensure that errors in Skuldsku does not prevent the application
         *    from running normally.
         * 2. Ensure that even Exceptions are handled in the same manner as
         *    if Skuldsku was not present.
         */

        boolean doFilterBegun = false;
        IOException filterIOException = null;
        ServletException filterServletException = null;
        RuntimeException filterRuntimeException = null;
        try {
            final HttpServletRequest req = (HttpServletRequest) request;
            final HttpServletResponse resp = (HttpServletResponse) response;

            final HttpCall httpCall = new HttpCall();
            httpCall.setMethod(req.getMethod());
            httpCall.setStartTime(System.currentTimeMillis());
            recordPath(req, httpCall);

            RequestWrapper requestSpy = new RequestWrapper(req, httpCall);
            ResponseWrapper responseSpy = new ResponseWrapper(resp);

            logHeaders(req, httpCall);
            final String requestId = UUID.randomUUID().toString();
            ClientIdentifierHolder.setClientIdentifier(requestId);
            httpCall.setClientIdentifier(requestId);

            doFilterBegun = true;
            try {
                chain.doFilter(requestSpy,responseSpy);
            } catch (IOException e) {
                filterIOException = e;
            } catch (ServletException e) {
                filterServletException = e;
            } catch (RuntimeException e) {
                filterRuntimeException = e;
            }
        
            httpCall.setEndTime(System.currentTimeMillis());
            httpCall.setOutput(responseSpy.getWritten());
            httpCall.setStatus(resp.getStatus());
            httpCall.setResponseHeaders(getResponseHeaders(resp));

            HttpCallPersister reporter = getReporter();
            if (reporter != null) {
                reporter.reportCall(httpCall);
            } else {
                RecorderLog.error("There is no CallReporter associated with the current HTTP filter. HTTP interactions will not be recorded.");
            }
        } catch (Exception e) {
            RecorderLog.error("Exception while recording request", e);
            if (!doFilterBegun) {
                chain.doFilter(request, response);
            }
        } finally {
            try {
                ClientIdentifierHolder.removeClientIdentifier();
            } catch (RuntimeException e) {
                RecorderLog.error("Cannot remove clientIdentifiers: Might cause (slow) memory leak.", e);
            }
            if (filterIOException != null) {
                throw filterIOException;
            }
            if (filterServletException != null) {
                throw filterServletException;
            }
            if (filterRuntimeException != null) {
                throw filterRuntimeException;
            }
        }
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
        final String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            path.append(pathInfo);
        }
        
        final String queryString = req.getQueryString();
        if (queryString != null) {
            path.append("?");
            path.append(queryString);
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
