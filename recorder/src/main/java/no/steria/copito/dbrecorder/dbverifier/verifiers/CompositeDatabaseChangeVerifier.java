package no.steria.copito.dbrecorder.dbverifier.verifiers;

import java.util.ArrayList;
import java.util.List;

import no.steria.copito.dbrecorder.dbchange.DatabaseChange;
import no.steria.copito.dbrecorder.dbverifier.DatabaseChangeVerifier;
import no.steria.copito.dbrecorder.dbverifier.VerifierOptions;
import no.steria.copito.dbrecorder.dbverifier.VerifierResult;

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
