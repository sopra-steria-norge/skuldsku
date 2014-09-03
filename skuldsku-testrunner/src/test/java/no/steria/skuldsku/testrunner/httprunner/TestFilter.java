package no.steria.skuldsku.testrunner.httprunner;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import no.steria.skuldsku.recorder.httprecorder.CallReporter;
import no.steria.skuldsku.recorder.httprecorder.SkuldskuFilter;

public class TestFilter extends SkuldskuFilter {
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
