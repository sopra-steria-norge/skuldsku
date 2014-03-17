package no.steria.httpspy;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public abstract class ServletFilter implements Filter{


    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ReportObject reportObject = new ReportObject();
        RequestWrapper requestSpy = new RequestWrapper((HttpServletRequest) request, reportObject);
        chain.doFilter(requestSpy,response);
        getReporter().reportCall(reportObject);
    }

    public abstract CallReporter getReporter();



}
