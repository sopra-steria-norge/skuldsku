package no.steria.httpspy;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public abstract class ServletFilter implements Filter{


    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        RequestWrapper requestSpy = new RequestWrapper((HttpServletRequest) request, getReporter());
        chain.doFilter(requestSpy,response);
    }

    public abstract CallReporter getReporter();

}
