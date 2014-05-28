package utils;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import utils.SqlUtils;

public class SqlUtilsTest {

    @Test
    public void readSqlTestSimple() throws Exception {
        Assert.assertEquals("SELECT *\nFROM ::TEST::", SqlUtils.readSql("sqlUtilsTest.sql"));
    }
    
    @Test
    public void readSqlTestReplace() throws Exception {
        final Map<String, String> replace = new HashMap<String, String>();
        replace.put("::TEST::", "lala");
        Assert.assertEquals("SELECT *\nFROM lala", SqlUtils.readSql("sqlUtilsTest.sql", replace));
    }
    
    @Test(expected=IllegalStateException.class)
    public void runtimeExceptionForNonExistingFile() throws IllegalStateException {
        System.out.println(SqlUtils.readSql("nonExistingTestFile.sql"));
        Assert.fail();
    }
}
