package no.steria.skuldsku.testrunner.dbrunner.dbverifier.verifiers;

import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifierOptions;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifierResult;
import org.junit.Test;

import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;


public class BestFitDatabaseChangeVerifierTest {
    
    private static final BestFitDatabaseChangeVerifier verifier = new BestFitDatabaseChangeVerifier();
    
    @Test
    public void emptyIsOk() {
        final DatabaseChangeVerifierResult result = verifier.assertEquals(Collections.<DatabaseChange>emptyList(),
                Collections.<DatabaseChange>emptyList(),
                new DatabaseChangeVerifierOptions());
        assertThat(result.hasErrors()).isFalse();
    }
    
    @Test
    public void singleLineDifference() {
        final DatabaseChangeVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                Collections.<DatabaseChange>emptyList(),
                new DatabaseChangeVerifierOptions());
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(1);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(0);
        assertThat(result.getNotEquals().size()).isEqualTo(0);
    }
    
    @Test
    public void singleLineMatches() {
        final DatabaseChangeVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                new DatabaseChangeVerifierOptions());
        assertThat(result.hasErrors()).isFalse();
    }
    
    @Test
    public void twoLineMatchesWithSameOrder() {
        final DatabaseChangeVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                new DatabaseChangeVerifierOptions());
        assertThat(result.hasErrors()).isFalse();
    }
    
    @Test
    public void twoLineMatchesWithDifferentOrder() {
        final DatabaseChangeVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                new DatabaseChangeVerifierOptions());
        assertThat(result.hasErrors()).isFalse();
    }
    
    @Test
    public void missingFromActual() {
        final DatabaseChangeVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                new DatabaseChangeVerifierOptions());
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(1);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(0);
        assertThat(result.getNotEquals().size()).isEqualTo(0);
    }
    
    @Test
    public void additionalInActual() {
        final DatabaseChangeVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar" }),
                new DatabaseChangeVerifierOptions());
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(0);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(1);
        assertThat(result.getNotEquals().size()).isEqualTo(0);
    }
    
    @Test
    public void notEquals() {
        final DatabaseChangeVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=tja", "foo=bar" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                new DatabaseChangeVerifierOptions());
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(0);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(0);
        assertThat(result.getNotEquals().size()).isEqualTo(1);
        
        assertThat(result.getNotEquals().get(0).getExpected().toString()).isEqualTo("lala=tja");
        assertThat(result.getNotEquals().get(0).getActual().toString()).isEqualTo("lala=nope");
    }
    
    @Test
    public void additionalInActualWithCorrectPriority() {
        final DatabaseChangeVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=2" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=1;b=2;c=3;d=4", "a=2"}),
                new DatabaseChangeVerifierOptions());
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(0);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(1);
        assertThat(result.getNotEquals().size()).isEqualTo(0);
        
        assertThat(result.getAdditionalInActual().get(0).toString()).isEqualTo("a=1;b=2;c=3;d=4");
    }
    
    @Test
    public void missingFromActualWithCorrectPriority() {
        final DatabaseChangeVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=1;b=2;c=3;d=4", "a=2" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=2" }),
                new DatabaseChangeVerifierOptions());
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(1);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(0);
        assertThat(result.getNotEquals().size()).isEqualTo(0);
        
        assertThat(result.getMissingFromActual().get(0).toString()).isEqualTo("a=1;b=2;c=3;d=4");
    }
    
    @Test
    public void equalWhenFieldSkipped() {
        final DatabaseChangeVerifierOptions databaseChangeVerifierOptions = new DatabaseChangeVerifierOptions();
        databaseChangeVerifierOptions.addSkipField("c");
        
        final DatabaseChangeVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=5" }),
                databaseChangeVerifierOptions);
        assertThat(!result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(0);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(0);
        assertThat(result.getNotEquals().size()).isEqualTo(0);
    }
}
