package no.steria.skuldsku.testrunner.dbrunner.testrunner;

import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifierResult;

import java.util.ArrayList;
import java.util.List;

public class ConsoleVerifierResultHandler implements VerifierResultHandler {

    @Override
    public void handle(DatabaseChangeVerifierResult result) {
        final List<String> list = new ArrayList<>();
        for (DatabaseChange expectedDatabaseChange : result.getMissingFromActual()) {
            list.add("Cannot find actual data matching expected data on line: " + expectedDatabaseChange.getLineNumber());
        }
        for (DatabaseChangeVerifierResult.VerifierResultPair pair : result.getNotEquals()) {
            list.add("The actual data on line " + pair.getActual().getLineNumber()
                    + " does not match the expected data on line number " + pair.getExpected().getLineNumber());
        }
        for (DatabaseChange unmatchedCandidate : result.getAdditionalInActual()) {
            list.add("Could not find expected data matching actual data on line " + unmatchedCandidate.getLineNumber());
        }
        for (String failMessage : result.getAssertionFailed()) {
            list.add(failMessage);
        }
        for (String s : list) {
            System.out.println(s);
        }
    }
    

}
