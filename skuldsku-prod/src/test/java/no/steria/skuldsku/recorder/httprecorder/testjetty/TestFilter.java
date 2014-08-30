package no.steria.skuldsku.recorder.httprecorder.testjetty;

import no.steria.skuldsku.recorder.httprecorder.CallReporter;
import no.steria.skuldsku.recorder.httprecorder.SkuldskuFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public class TestFilter extends SkuldskuFilter {
    private static CallReporter myReporter;

    public TestFilter() {
    }

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
