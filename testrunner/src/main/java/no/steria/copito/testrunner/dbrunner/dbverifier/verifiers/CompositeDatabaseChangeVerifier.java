package no.steria.copito.testrunner.dbrunner.dbverifier.verifiers;

import java.util.ArrayList;
import java.util.List;

import no.steria.copito.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.copito.testrunner.dbrunner.dbverifier.DatabaseChangeVerifier;
import no.steria.copito.testrunner.dbrunner.dbverifier.VerifierOptions;
import no.steria.copito.testrunner.dbrunner.dbverifier.VerifierResult;

public class
        CompositeDatabaseChangeVerifier implements DatabaseChangeVerifier {

    private final DatabaseChangeVerifier[] verifiers;
    
    public CompositeDatabaseChangeVerifier(DatabaseChangeVerifier... verifiers) {
        this.verifiers = verifiers;
    }
    
    @Override
    public VerifierResult assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, VerifierOptions verifierOptions) {
        final List<VerifierResult> result = new ArrayList<VerifierResult>();
        for (DatabaseChangeVerifier verifier : verifiers) {
            result.add(verifier.assertEquals(expected, actual, verifierOptions));
        }
        return new VerifierResult(result);
    }

}
