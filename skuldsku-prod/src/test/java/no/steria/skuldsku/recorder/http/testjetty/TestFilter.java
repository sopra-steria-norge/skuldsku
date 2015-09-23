package no.steria.skuldsku.recorder.http.testjetty;

import no.steria.skuldsku.recorder.http.HttpCallPersister;
import no.steria.skuldsku.recorder.http.SkuldskuFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public class TestFilter extends SkuldskuFilter {
    private static HttpCallPersister myReporter;

    public TestFilter() {
    }

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
