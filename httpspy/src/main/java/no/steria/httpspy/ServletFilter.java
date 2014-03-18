package no.steria.httpspy;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class ServletFilter implements Filter{


    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        ReportObject reportObject = new ReportObject();
        reportObject.setMethod(req.getMethod());
        reportObject.setPath(req.getServletPath() + req.getPathInfo());

        RequestWrapper requestSpy = new RequestWrapper(req, reportObject);

        HttpServletResponse resp = (HttpServletResponse) response;

        ResponseWrapper responseSpy = new ResponseWrapper(resp);

        chain.doFilter(requestSpy,responseSpy);

        reportObject.setOutput(responseSpy.getWritten());

        getReporter().reportCall(reportObject);
    }

    public abstract CallReporter getReporter();



}
