package no.steria.copito.testrunner.dbrunner.dbverifier;

import java.util.List;

import no.steria.copito.testrunner.dbrunner.dbchange.DatabaseChange;

public interface DatabaseChangeVerifier {

    public VerifierResult assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, VerifierOptions verifierOptions);
    
}
 
