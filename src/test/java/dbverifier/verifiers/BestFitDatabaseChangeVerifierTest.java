package dbverifier.verifiers;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Test;

import dbchange.DatabaseChange;
import dbverifier.VerifierResult;
import dbverifier.verifiers.BestFitDatabaseChangeVerifier;

public class BestFitDatabaseChangeVerifierTest {
    
    private static final BestFitDatabaseChangeVerifier verifier = new BestFitDatabaseChangeVerifier();
    
    @Test
    public void emptyIsOk() {
        final VerifierResult result = verifier.assertEquals(Collections.<DatabaseChange>emptyList(),
                Collections.<DatabaseChange>emptyList(),
                Collections.<String>emptySet());
        Assert.assertFalse("Expecting no errors", result.hasErrors());
    }
    
    @Test
    public void singleLineDifference() {
        final VerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                Collections.<DatabaseChange>emptyList(),
                Collections.<String>emptySet());
        Assert.assertTrue("Expecting an error", result.hasErrors());
        Assert.assertEquals(1, result.getMissingFromActual().size());
        Assert.assertEquals(0, result.getAdditionalInActual().size());
        Assert.assertEquals(0, result.getNotEquals().size());
    }
    
    @Test
    public void singleLineMatches() {
        final VerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                Collections.<String>emptySet());
        Assert.assertFalse("Expecting no errors", result.hasErrors());
    }
    
    @Test
    public void twoLineMatchesWithSameOrder() {
        final VerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                Collections.<String>emptySet());
        Assert.assertFalse("Expecting no errors", result.hasErrors());
    }
    
    @Test
    public void twoLineMatchesWithDifferentOrder() {
        final VerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                Collections.<String>emptySet());
        Assert.assertFalse("Expecting no errors", result.hasErrors());
    }
    
    @Test
    public void missingFromActual() {
        final VerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                Collections.<String>emptySet());
        Assert.assertTrue("Expecting an error", result.hasErrors());
        Assert.assertEquals(1, result.getMissingFromActual().size());
        Assert.assertEquals(0, result.getAdditionalInActual().size());
        Assert.assertEquals(0, result.getNotEquals().size());
    }
    
    @Test
    public void additionalInActual() {
        final VerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "lala=nope", "foo=bar" }),
                Collections.<String>emptySet());
        Assert.assertTrue("Expecting an error", result.hasErrors());
        Assert.assertEquals(0, result.getMissingFromActual().size());
        Assert.assertEquals(1, result.getAdditionalInActual().size());
        Assert.assertEquals(0, result.getNotEquals().size());
    }
    
    @Test
    public void notEquals() {
        final VerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "lala=tja", "foo=bar" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "foo=bar", "lala=nope" }),
                Collections.<String>emptySet());
        Assert.assertTrue("Expecting an error", result.hasErrors());
        Assert.assertEquals(0, result.getMissingFromActual().size());
        Assert.assertEquals(0, result.getAdditionalInActual().size());
        Assert.assertEquals(1, result.getNotEquals().size());
        
        Assert.assertEquals("lala=tja", result.getNotEquals().get(0).getExpected().toString());
        Assert.assertEquals("lala=nope", result.getNotEquals().get(0).getActual().toString());
    }
    
    @Test
    public void additionalInActualWithCorrectPriority() {
        final VerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=2" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=1;b=2;c=3;d=4", "a=2"}),
                Collections.<String>emptySet());
        Assert.assertTrue("Expecting an error", result.hasErrors());
        Assert.assertEquals(0, result.getMissingFromActual().size());
        Assert.assertEquals(1, result.getAdditionalInActual().size());
        Assert.assertEquals(0, result.getNotEquals().size());
        
        Assert.assertEquals("a=1;b=2;c=3;d=4", result.getAdditionalInActual().get(0).toString());
    }
    
    @Test
    public void missingFromActualWithCorrectPriority() {
        final VerifierResult result = verifier.assertEquals(DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=1;b=2;c=3;d=4", "a=2" }),
                DatabaseChange.toDatabaseChangeList(new String[] { "a=1;b=2;c=3;d=4", "a=2" }),
                Collections.<String>emptySet());
        Assert.assertTrue("Expecting an error", result.hasErrors());
        Assert.assertEquals(1, result.getMissingFromActual().size());
        Assert.assertEquals(0, result.getAdditionalInActual().size());
        Assert.assertEquals(0, result.getNotEquals().size());
        
        Assert.assertEquals("a=1;b=2;c=3;d=4", result.getMissingFromActual().get(0).toString());
    }
}
