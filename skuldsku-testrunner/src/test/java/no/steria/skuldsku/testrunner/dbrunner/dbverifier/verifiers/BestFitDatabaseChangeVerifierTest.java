package no.steria.skuldsku.testrunner.dbrunner.dbverifier.verifiers;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collections;

import no.steria.skuldsku.common.result.Results;
import no.steria.skuldsku.testrunner.common.ClientIdentifierMapper;
import no.steria.skuldsku.testrunner.dbrunner.dbchange.DatabaseChange;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.DatabaseChangeVerifierOptions;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeAdditionalInActualResult;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeMatchesResult;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeMissingFromActualResult;
import no.steria.skuldsku.testrunner.dbrunner.dbverifier.result.DatabaseChangeNotEqualsResult;

import org.junit.Test;


public class BestFitDatabaseChangeVerifierTest {
    
    private static final BestFitDatabaseChangeVerifier verifier = new BestFitDatabaseChangeVerifier();
    
    @Test
    public void emptyIsOk() {
        final Results results = verifier.assertEquals(Collections.<DatabaseChange>emptyList(),
                Collections.<DatabaseChange>emptyList(),
                new DatabaseChangeVerifierOptions());
        assertThat(results.hasErrors()).isFalse();
    }
    
    @Test
    public void singleLineDifference() {
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                Collections.<DatabaseChange>emptyList(),
                new DatabaseChangeVerifierOptions());
        assertThat(results.hasErrors()).isTrue();
        assertThat(results.getByType(DatabaseChangeMissingFromActualResult.class).size()).isEqualTo(1);
        assertThat(results.getByType(DatabaseChangeAdditionalInActualResult.class).size()).isEqualTo(0);
        assertThat(results.getByType(DatabaseChangeNotEqualsResult.class).size()).isEqualTo(0);
    }
    
    @Test
    public void singleLineMatches() {
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                new DatabaseChangeVerifierOptions());
        assertThat(results.hasErrors()).isFalse();
    }
    
    @Test
    public void twoLineMatchesWithSameOrder() {
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                new DatabaseChangeVerifierOptions());
        assertThat(results.hasErrors()).isFalse();
    }
    
    @Test
    public void twoLineMatchesWithDifferentOrder() {
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                new DatabaseChangeVerifierOptions());
        assertThat(results.hasErrors()).isFalse();
    }
    
    @Test
    public void prioritizeShorterMatches() {
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar;lala=nope", "foo=bar" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar" }),
                new DatabaseChangeVerifierOptions());
        assertThat(results.getResults().size()).isEqualTo(3);
        assertThat(results.getByType(DatabaseChangeMatchesResult.class).size()).isEqualTo(2);
        assertThat(results.getByType(DatabaseChangeMissingFromActualResult.class).size()).isEqualTo(1);
    }
    
    @Test
    public void prioritizeFirstDatabaseChangesInExpected() {
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=1", "a=0" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=2" }),
                new DatabaseChangeVerifierOptions());
        assertThat(results.getResults().size()).isEqualTo(2);
        assertThat(results.getByType(DatabaseChangeNotEqualsResult.class).size()).isEqualTo(1); // Matched with a=1
        assertThat(results.getByType(DatabaseChangeMissingFromActualResult.class).get(0).getItem().getValue("a")).isEqualTo("0");
    }
    
    @Test
    public void prioritizeFirstDatabaseChangesInActual() {
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=2" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=1", "a=0" }),
                new DatabaseChangeVerifierOptions());
        assertThat(results.getResults().size()).isEqualTo(2);
        assertThat(results.getByType(DatabaseChangeNotEqualsResult.class).size()).isEqualTo(1); // Matched with a=1
        assertThat(results.getByType(DatabaseChangeAdditionalInActualResult.class).get(0).getItem().getValue("a")).isEqualTo("0");
    }
    
    @Test
    public void missingFromActual() {
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                new DatabaseChangeVerifierOptions());
        assertThat(results.hasErrors()).isTrue();
        assertThat(results.getByType(DatabaseChangeMissingFromActualResult.class).size()).isEqualTo(1);
        assertThat(results.getByType(DatabaseChangeAdditionalInActualResult.class).size()).isEqualTo(0);
        assertThat(results.getByType(DatabaseChangeNotEqualsResult.class).size()).isEqualTo(0);
    }
    
    @Test
    public void additionalInActual() {
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar" }),
                new DatabaseChangeVerifierOptions());
        assertThat(results.hasErrors()).isTrue();
        assertThat(results.getByType(DatabaseChangeMissingFromActualResult.class).size()).isEqualTo(0);
        assertThat(results.getByType(DatabaseChangeAdditionalInActualResult.class).size()).isEqualTo(1);
        assertThat(results.getByType(DatabaseChangeNotEqualsResult.class).size()).isEqualTo(0);
    }
    
    @Test
    public void notEquals() {
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=tja", "foo=bar" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                new DatabaseChangeVerifierOptions());
        assertThat(results.hasErrors()).isTrue();
        assertThat(results.getByType(DatabaseChangeMissingFromActualResult.class).size()).isEqualTo(0);
        assertThat(results.getByType(DatabaseChangeAdditionalInActualResult.class).size()).isEqualTo(0);
        assertThat(results.getByType(DatabaseChangeNotEqualsResult.class).size()).isEqualTo(1);
        
        assertThat(results.getByType(DatabaseChangeNotEqualsResult.class).get(0).getExpected().toString()).isEqualTo("lala=tja");
        assertThat(results.getByType(DatabaseChangeNotEqualsResult.class).get(0).getActual().toString()).isEqualTo("lala=nope");
    }
    
    @Test
    public void additionalInActualWithCorrectPriority() {
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=2" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=1;b=2;c=3;d=4", "a=2"}),
                new DatabaseChangeVerifierOptions());
        assertThat(results.hasErrors()).isTrue();
        assertThat(results.getByType(DatabaseChangeMissingFromActualResult.class).size()).isEqualTo(0);
        assertThat(results.getByType(DatabaseChangeAdditionalInActualResult.class).size()).isEqualTo(1);
        assertThat(results.getByType(DatabaseChangeNotEqualsResult.class).size()).isEqualTo(0);
        
        assertThat(results.getByType(DatabaseChangeAdditionalInActualResult.class).get(0).getItem().toString()).isEqualTo("a=1;b=2;c=3;d=4");
    }
    
    @Test
    public void missingFromActualWithCorrectPriority() {
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=1;b=2;c=3;d=4", "a=2" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=2" }),
                new DatabaseChangeVerifierOptions());
        assertThat(results.hasErrors()).isTrue();
        assertThat(results.getByType(DatabaseChangeMissingFromActualResult.class).size()).isEqualTo(1);
        assertThat(results.getByType(DatabaseChangeAdditionalInActualResult.class).size()).isEqualTo(0);
        assertThat(results.getByType(DatabaseChangeNotEqualsResult.class).size()).isEqualTo(0);
        
        assertThat(results.getByType(DatabaseChangeMissingFromActualResult.class).get(0).getItem().toString()).isEqualTo("a=1;b=2;c=3;d=4");
    }
    
    @Test
    public void equalWhenFieldSkipped() {
        final DatabaseChangeVerifierOptions databaseChangeVerifierOptions = new DatabaseChangeVerifierOptions();
        databaseChangeVerifierOptions.addSkipField("c");
        
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=5" }),
                databaseChangeVerifierOptions);
        assertThat(!results.hasErrors()).isTrue();
        assertThat(results.getByType(DatabaseChangeMissingFromActualResult.class).size()).isEqualTo(0);
        assertThat(results.getByType(DatabaseChangeAdditionalInActualResult.class).size()).isEqualTo(0);
        assertThat(results.getByType(DatabaseChangeNotEqualsResult.class).size()).isEqualTo(0);
    }
    
    @Test
    public void allSkippedMatchesOk() {
        final DatabaseChangeVerifierOptions databaseChangeVerifierOptions = new DatabaseChangeVerifierOptions();
        databaseChangeVerifierOptions.addSkipField("a");
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=1", "a=2" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=3", "a=4" }),
                databaseChangeVerifierOptions);
        assertThat(results.hasErrors()).isFalse();
    }
    
    @Test
    public void clientIdentifierCheck() {
        final DatabaseChangeVerifierOptions databaseChangeVerifierOptions = new DatabaseChangeVerifierOptions();
        databaseChangeVerifierOptions.setClientIdentifierMapper(new ClientIdentifierMapper() {
            @Override
            public String translateToActual(String expectedClientIdentifier) {
                if (expectedClientIdentifier.equals("foo")) {
                    return "bar";
                } else {
                    return expectedClientIdentifier;
                }
            }
        });
        
        final Results results = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "CLIENT_IDENTIFIER=foo;a=2" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "CLIENT_IDENTIFIER=nope;a=2", "CLIENT_IDENTIFIER=bar;a=1" }),
                databaseChangeVerifierOptions);
        assertThat(results.hasErrors()).isTrue();
        assertThat(results.getResults().size()).isEqualTo(2);
        assertThat(results.getByType(DatabaseChangeNotEqualsResult.class).size()).isEqualTo(1);
        assertThat(results.getByType(DatabaseChangeAdditionalInActualResult.class).get(0).getItem().getValue("a")).isEqualTo("2");
    }
}
