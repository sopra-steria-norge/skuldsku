package no.steria.skuldsku.testrunner.httprunner.result;

import no.steria.skuldsku.common.result.ComparisionResult;
import no.steria.skuldsku.recorder.http.HttpCall;

public class HttpCallOutputNotEqualsResult implements ComparisionResult<HttpCall>{

    private final HttpCall expected;
    private final HttpCall actual;
    private final String expectedOutputText;
    private final String actualOutputText;
    
    public HttpCallOutputNotEqualsResult(HttpCall expected, HttpCall actual, String expectedOutputText, String actualOutputText) {
        this.expected = expected;
        this.actual = actual;
        this.expectedOutputText = expectedOutputText;
        this.actualOutputText = actualOutputText;
        
        if (actual.getStartTime() == null) {
            throw new NullPointerException("actual.getStartTime() == null");
        }
        if (actual.getClientIdentifier() == null) {
            throw new NullPointerException("actual.getClientIdentifier() == null");
        }
        if (actual.getPath() == null) {
            throw new NullPointerException("actual.getPath() == null");
        }
    }
    
    
    @Override
    public String getStartTime() {
        return actual.getStartTime();
    }
    
    @Override
    public String getRequestId() {
        return actual.getClientIdentifier();
    }
    
    @Override
    public String getTitle() {
        return "HTTP " + getActual().getMethod() + " " + getActual().getPath() + " actual response data does not match expected";
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public String getExplanation() {
        return "Actual response data does not match expected.";
    }

    @Override
    public HttpCall getExpected() {
        return expected;
    }

    @Override
    public HttpCall getActual() {
        return actual;
    }
    
    @Override
    public String toString() {
        if (expectedOutputText != null && actualOutputText != null) {
            return getExplanation() + " " + findFirstDifferences(expectedOutputText, actualOutputText)
                        + "\n\n\n\n================= EXPECTED ========================\n" + expected.getOutputAsText()
                        + "\n\n\n\n=================  ACTUAL  ========================\n" + actual.getOutputAsText();
        } else {
            return getExplanation();
        }
    }

    private static String findFirstDifferences(String expected, String actual) {
        assert !expected.equals(actual);
        
        int startIndex;
        for (startIndex=0; startIndex<Math.min(expected.length(), actual.length()); startIndex++) {
            if (expected.charAt(startIndex) != actual.charAt(startIndex)) {
                break;
            }
        }
        
        if (startIndex >= expected.length()) {
            return "Actual content longer than expected.";
        }
        if (startIndex >= actual.length()) {
            return "Expected content longer than actual";
        }
        
        return "First difference. Expected:\n" + relevantSubstring(expected, startIndex) + "\nGot:\n" + relevantSubstring(actual, startIndex);
    }
    
    private static String relevantSubstring(String text, int startIndex) {
        final int BEFORE = 100;
        final int MATCH_AND_AFTER = 100;
        return text.substring(Math.max(0, startIndex-BEFORE), Math.min(text.length(), startIndex+MATCH_AND_AFTER));
    }
}
