package no.steria.skuldsku.testrunner.httprunner;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import no.steria.skuldsku.recorder.http.HttpCallPersister;
import no.steria.skuldsku.recorder.http.SkuldskuFilter;

public class TestFilter extends SkuldskuFilter {
    private static HttpCallPersister myReporter;

    public static void setReporter(HttpCallPersister reporter) {
        myReporter = reporter;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public HttpCallPersister getReporter() {
        return myReporter;
    }
}
