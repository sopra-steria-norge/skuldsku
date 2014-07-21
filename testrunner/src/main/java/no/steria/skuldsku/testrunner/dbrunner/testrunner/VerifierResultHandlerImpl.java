package no.steria.skuldsku.testrunner.dbrunner.testrunner;

import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.VerifierResult;

public class VerifierResultHandlerImpl implements VerifierResultHandler {

    @Override
    public void handle(VerifierResult result) {
        for (DatabaseChange expectedDatabaseChange : result.getMissingFromActual()) {
            throw new IllegalStateException("Cannot find actual data matching expected data on line: " + expectedDatabaseChange.getLineNumber());
        }
        for (VerifierResult.VerifierResultPair pair : result.getNotEquals()) {
            throw new IllegalStateException("The actual data on line " + pair.getActual().getLineNumber()
                    + " does not match the expected data on line number " + pair.getExpected().getLineNumber());
        }
        for (DatabaseChange unmatchedCandidate : result.getAdditionalInActual()) {
            throw new IllegalStateException("Could not find expected data matching actual data on line " + unmatchedCandidate.getLineNumber());
        }
        for (String failMessage : result.getAssertionFailed()) {
            throw new IllegalStateException(failMessage);
        }
    }
    

}
