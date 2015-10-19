package no.steria.skuldsku.testrunner.httprunner.result;

import java.util.List;

import no.steria.skuldsku.common.result.Results;
import no.steria.skuldsku.recorder.http.HttpCall;

public final class HttpCallResultsFactory {

    public static Results createFrom(List<HttpCall> expectedHttpCalls, List<HttpCall> actualHttpCalls) {
        // TODO: Proper implementation.
        if (expectedHttpCalls.size() != actualHttpCalls.size()) {
            throw new ArrayIndexOutOfBoundsException("expectedHttpCalls.size() != actualHttpCalls.size() --> " + expectedHttpCalls.size() + " != " + actualHttpCalls.size());
        }
        final Results results = new Results();
        for (int i=0; i<expectedHttpCalls.size(); i++) {
            final HttpCall expected = expectedHttpCalls.get(i);
            final HttpCall actual = actualHttpCalls.get(i);
            final int requestNumber = i+1;
            results.addResult(new HttpCallResult(expected, actual, requestNumber));
        }
        return results;
    }
}