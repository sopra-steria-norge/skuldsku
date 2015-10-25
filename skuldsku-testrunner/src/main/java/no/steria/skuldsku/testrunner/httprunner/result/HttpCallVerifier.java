package no.steria.skuldsku.testrunner.httprunner.result;

import java.util.List;
import java.util.Set;

import no.steria.skuldsku.common.result.Results;
import no.steria.skuldsku.recorder.http.HttpCall;

public final class HttpCallVerifier {

    public static Results createFrom(List<HttpCall> expectedHttpCalls, List<HttpCall> actualHttpCalls, HttpCallVerifierOptions options) {
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
        
        final List<HttpCallResult> httpCalls = results.getByType(HttpCallResult.class);
        generateStatusCodeResults(results, httpCalls);
        generateHeaderResults(results, httpCalls);
        generateOutputResults(results, httpCalls, options);
        
        return results;
    }

    private static void generateStatusCodeResults(Results results, List<HttpCallResult> httpCalls) {
        for (HttpCallResult httpCall : httpCalls) {
            final HttpCall expected = httpCall.getExpected();
            final HttpCall actual = httpCall.getActual();
            if (expected.getStatus() != 0
                    && actual.getStatus() != 0
                    && expected.getStatus() != actual.getStatus()) {
                results.addResult(new HttpCallStatusCodesNotEqualsResult(expected, actual));
            }
        }
    }
    
    private static void generateHeaderResults(Results results, List<HttpCallResult> httpCalls) {
        for (HttpCallResult httpCall : httpCalls) {
            final HttpCall expected = httpCall.getExpected();
            final HttpCall actual = httpCall.getActual();
            
            final String expectedHeaders = HttpCallHeadersNotEqualsResult.getFiltredResponseHeadersAsString(expected.getResponseHeaders());
            final String actualHeaders = HttpCallHeadersNotEqualsResult.getFiltredResponseHeadersAsString(actual.getResponseHeaders());
            if (!expectedHeaders.equals(actualHeaders)) {
                results.addResult(new HttpCallHeadersNotEqualsResult(expected, actual));
            }
        }
    }
    
    private static void generateOutputResults(Results results, List<HttpCallResult> httpCalls, HttpCallVerifierOptions options) {
        for (HttpCallResult httpCall : httpCalls) {
            final HttpCall expected = httpCall.getExpected();
            final HttpCall actual = httpCall.getActual();
            
            if (matches(expected.getPath(), options.getOutputComparisionSkipPaths())) {
                continue;
            }
            
            String expectedOutputText = expected.getOutputAsText();
            String actualOutputText = actual.getOutputAsText();
            if (expectedOutputText == null || actualOutputText == null) {
                final String expectedOutput = expected.getOutput();
                final String actualOutput = actual.getOutput();
                if (expectedOutput == null && actualOutput != null
                        || expectedOutput != null && actualOutput == null
                        || expectedOutput != null && !expectedOutput.equals(actualOutput)) {
                    results.addResult(new HttpCallOutputNotEqualsResult(expected, actual, null, null));
                }
            } else {
                expectedOutputText = removeIgnoredParts(expectedOutputText, options.getOutputComparisionIgnores());
                actualOutputText = removeIgnoredParts(actualOutputText, options.getOutputComparisionIgnores());
                if (!expectedOutputText.equals(actualOutputText)) {
                    results.addResult(new HttpCallOutputNotEqualsResult(expected, actual, expectedOutputText, actualOutputText));
                }
            }
        }
    }
    
    private static boolean matches(String s, Set<String> regexs) {
        for (String r : regexs) {
            if (s.matches(r)) {
                return true;
            }
        }
        return false;
    }

    private static String removeIgnoredParts(String s, final List<String> ignoreRegex) {
        for (String key : ignoreRegex) {
            s = s.replaceAll(key, "");
        }
        return s;
    }
}
