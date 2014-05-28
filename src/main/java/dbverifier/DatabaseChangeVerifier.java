package dbverifier;

import java.util.List;

import dbchange.DatabaseChange;

public interface DatabaseChangeVerifier {

    public VerifierResult assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, VerifierOptions verifierOptions);
    
}
 