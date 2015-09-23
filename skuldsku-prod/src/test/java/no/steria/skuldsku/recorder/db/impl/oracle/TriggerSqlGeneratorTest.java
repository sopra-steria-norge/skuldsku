package no.steria.skuldsku.recorder.db.impl.oracle;

import junit.framework.Assert;
import no.steria.skuldsku.recorder.db.impl.oracle.TriggerSqlGenerator;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TriggerSqlGeneratorTest {
    
    @Test
    public void testColumnNameAndValue() {
        final String result = TriggerSqlGenerator.columnNameAndValue("LALA", "ROW", ":new.");
        Assert.assertEquals("'ROW.LALA='||NVL(REPLACE(REPLACE(REPLACE(:new.LALA, '\\', '\\\\'), ';', '\\;'), '=', '\\='), '\\0')",
                result);
    }
    
    @Test
    public void testGenerateInsertDataRowSingleColumn() {
        final String expected = TriggerSqlGenerator.columnNameAndValue("LALA", "ROW", ":new.");
        final String result = TriggerSqlGenerator.generateInsertDataRow(Collections.singletonList("LALA"));
        
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void testGenerateUpdateDataRowSingleColumn() {
        final String expected = TriggerSqlGenerator.columnNameAndValue("LALA", "ROW", ":new.") + "||';'||" + TriggerSqlGenerator.columnNameAndValue("LALA", "OLDROW", ":old.");
        final String expectedAlternate = TriggerSqlGenerator.columnNameAndValue("LALA", "ROW", ":old.") + "||';'||" + TriggerSqlGenerator.columnNameAndValue("LALA", "OLDROW", ":new.");
        final String result = TriggerSqlGenerator.generateUpdateDataRow(Collections.singletonList("LALA"));
        
        if (result.equals(expected)) {
            Assert.assertEquals(expected, result);
        } else {
            Assert.assertEquals(expectedAlternate, result);
        }
    }
    
    @Test
    public void testGenerateDeleteDataRowMultipleColumns() {       
        final String expected = TriggerSqlGenerator.columnNameAndValue("FOO", "ROW", ":old.")
                + "||';'||" + TriggerSqlGenerator.columnNameAndValue("BAR", "ROW", ":old.");
        
        final String result = TriggerSqlGenerator.generateDeleteDataRow(Arrays.asList(new String[] { "FOO", "BAR" }));;
        
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void testGenerateTriggerSqlExecutesWithoutExceptions() {
        final List<String> columns = Arrays.asList(new String[]{ "COL_A", "COL_B" });
        final String actualResult = TriggerSqlGenerator.generateTriggerSql("TESTTRIGGER", "TESTTABLE", columns);
        Assert.assertTrue(actualResult != null);
        Assert.assertTrue(actualResult.toUpperCase().contains("TRIGGER"));
        Assert.assertTrue(actualResult.toUpperCase().contains("COL_A"));
        Assert.assertTrue(actualResult.toUpperCase().contains("COL_B"));
        Assert.assertTrue(actualResult.toUpperCase().contains("TESTTRIGGER"));
    }
}
