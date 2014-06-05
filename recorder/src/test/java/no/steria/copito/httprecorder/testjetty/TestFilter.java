package no.steria.copito.httprecorder.testjetty;

import no.steria.copito.httprecorder.CallReporter;
import no.steria.copito.httprecorder.ServletFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public class TestFilter extends ServletFilter {
    private static CallReporter myReporter;

    public static void setReporter(CallReporter reporter) {
        myReporter = reporter;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public CallReporter getReporter() {
        return myReporter;
    }
}
