package dbverifier.verifiers;

import java.util.ArrayList;
import java.util.List;

import dbchange.DatabaseChange;
import dbverifier.DatabaseChangeVerifier;
import dbverifier.VerifierOptions;
import dbverifier.VerifierResult;

public class CompositeDatabaseChangeVerifier implements DatabaseChangeVerifier {

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
