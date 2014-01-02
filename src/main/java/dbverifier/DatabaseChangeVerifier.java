package dbverifier;

import java.util.List;
import java.util.Set;

import dbchange.DatabaseChange;

public interface DatabaseChangeVerifier {

    public VerifierResult assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, Set<String> skipFields);
    
}
 