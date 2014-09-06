package no.steria.skuldsku.testrunner.dbrunner.verifiers;

import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseVerifierOptions;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseVerifierResult;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.verifiers.BestFitDatabaseChangeVerifier;
import org.junit.Test;

import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;


public class BestFitDatabaseChangeVerifierTest {
    
    private static final BestFitDatabaseChangeVerifier verifier = new BestFitDatabaseChangeVerifier();
    
    @Test
    public void emptyIsOk() {
        final DatabaseVerifierResult result = verifier.assertEquals(Collections.<DatabaseChange>emptyList(),
                Collections.<DatabaseChange>emptyList(),
                new DatabaseVerifierOptions());
        assertThat(result.hasErrors()).isFalse();
    }
    
    @Test
    public void singleLineDifference() {
        final DatabaseVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                Collections.<DatabaseChange>emptyList(),
                new DatabaseVerifierOptions());
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(1);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(0);
        assertThat(result.getNotEquals().size()).isEqualTo(0);
    }
    
    @Test
    public void singleLineMatches() {
        final DatabaseVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                new DatabaseVerifierOptions());
        assertThat(result.hasErrors()).isFalse();
    }
    
    @Test
    public void twoLineMatchesWithSameOrder() {
        final DatabaseVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                new DatabaseVerifierOptions());
        assertThat(result.hasErrors()).isFalse();
    }
    
    @Test
    public void twoLineMatchesWithDifferentOrder() {
        final DatabaseVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                new DatabaseVerifierOptions());
        assertThat(result.hasErrors()).isFalse();
    }
    
    @Test
    public void missingFromActual() {
        final DatabaseVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                new DatabaseVerifierOptions());
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(1);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(0);
        assertThat(result.getNotEquals().size()).isEqualTo(0);
    }
    
    @Test
    public void additionalInActual() {
        final DatabaseVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar" }),
                new DatabaseVerifierOptions());
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(0);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(1);
        assertThat(result.getNotEquals().size()).isEqualTo(0);
    }
    
    @Test
    public void notEquals() {
        final DatabaseVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=tja", "foo=bar" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                new DatabaseVerifierOptions());
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(0);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(0);
        assertThat(result.getNotEquals().size()).isEqualTo(1);
        
        assertThat(result.getNotEquals().get(0).getExpected().toString()).isEqualTo("lala=tja");
        assertThat(result.getNotEquals().get(0).getActual().toString()).isEqualTo("lala=nope");
    }
    
    @Test
    public void additionalInActualWithCorrectPriority() {
        final DatabaseVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=2" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=1;b=2;c=3;d=4", "a=2"}),
                new DatabaseVerifierOptions());
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(0);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(1);
        assertThat(result.getNotEquals().size()).isEqualTo(0);
        
        assertThat(result.getAdditionalInActual().get(0).toString()).isEqualTo("a=1;b=2;c=3;d=4");
    }
    
    @Test
    public void missingFromActualWithCorrectPriority() {
        final DatabaseVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=1;b=2;c=3;d=4", "a=2" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=2" }),
                new DatabaseVerifierOptions());
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(1);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(0);
        assertThat(result.getNotEquals().size()).isEqualTo(0);
        
        assertThat(result.getMissingFromActual().get(0).toString()).isEqualTo("a=1;b=2;c=3;d=4");
    }
    
    @Test
    public void equalWhenFieldSkipped() {
        final DatabaseVerifierOptions databaseVerifierOptions = new DatabaseVerifierOptions();
        databaseVerifierOptions.addSkipField("c");
        
        final DatabaseVerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=5" }),
                databaseVerifierOptions);
        assertThat(!result.hasErrors()).isTrue();
        assertThat(result.getMissingFromActual().size()).isEqualTo(0);
        assertThat(result.getAdditionalInActual().size()).isEqualTo(0);
        assertThat(result.getNotEquals().size()).isEqualTo(0);
    }
}
