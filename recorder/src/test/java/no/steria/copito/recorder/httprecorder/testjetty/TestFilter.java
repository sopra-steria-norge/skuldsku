package no.steria.copito.recorder.httprecorder.testjetty;

import no.steria.copito.recorder.httprecorder.CallReporter;
import no.steria.copito.recorder.httprecorder.ServletFilter;

public class TestFilter extends ServletFilter {
    private static CallReporter myReporter;

    public TestFilter() {
    }

    public TestFilter(CallReporter callReporter) {
        myReporter = callReporter;
    }

    public static void setReporter(CallReporter reporter) {
        myReporter = reporter;
    }

    @Override
    public void destroy() {

    }

    @Override
    public CallReporter getReporter() {
        return myReporter;
    }
}
