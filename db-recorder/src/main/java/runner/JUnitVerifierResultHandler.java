package runner;

import junit.framework.Assert;
import dbchange.DatabaseChange;
import dbverifier.VerifierResult;
import dbverifier.VerifierResult.VerifierResultPair;

public class JUnitVerifierResultHandler implements VerifierResultHandler {

    @Override
    public void handle(VerifierResult result) {
        for (DatabaseChange expectedDatabaseChange : result.getMissingFromActual()) {
            Assert.fail("Cannot find actual data matching expected data on line: " + expectedDatabaseChange.getLineNumber());
        }
        for (VerifierResultPair pair : result.getNotEquals()) {
            Assert.fail("The actual data on line " + pair.getActual().getLineNumber()
                    + " does not match the expected data on line number " + pair.getExpected().getLineNumber());
        }
        for (DatabaseChange unmatchedCandidate : result.getAdditionalInActual()) {
            Assert.fail("Could not find expected data matching actual data on line " + unmatchedCandidate.getLineNumber());
        }
        for (String failMessage : result.getAssertionFailed()) {
            Assert.fail(failMessage);
        }
    }
    

}
