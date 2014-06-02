package no.steria.copito.dbrecorder.dbverifier;

import java.util.List;

import no.steria.copito.dbrecorder.dbchange.DatabaseChange;

public interface DatabaseChangeVerifier {

    public VerifierResult assertEquals(List<DatabaseChange> expected, List<DatabaseChange> actual, VerifierOptions verifierOptions);
    
}
 