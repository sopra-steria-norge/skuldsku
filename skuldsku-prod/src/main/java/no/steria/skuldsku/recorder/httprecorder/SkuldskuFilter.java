package no.steria.skuldsku.recorder.httprecorder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

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

    public static long getRequestId() {
        Long id = requestId.get();
        return id != null ? id : 0L;
    }

    private static final ThreadLocal<Long> requestId = new ThreadLocal<>();

    private static final AtomicLong nextId = new AtomicLong(0);


    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(!Skuldsku.isRecordingOn()){
            chain.doFilter(request, response);
            return;
        }
        long id = nextId.addAndGet(1);
        requestId.set(id);

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

        httpCall.setOutput(responseSpy.getWritten());
        
        ClientIdentifierHolder.removeClientIdentifier();

        HttpCallPersister reporter = getReporter();
        if (reporter != null) {
            reporter.reportCall(httpCall);
        } else {
            RecorderLog.error("There is no CallReporter associated with the current HTTP filter. HTTP interactions will not be recorded.");
        }
    }

    private void logHeaders(HttpServletRequest req, HttpCall httpCall) {
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
        httpCall.setHeaders(headers);
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
