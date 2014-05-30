package utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.junit.Test;

public class ParsedStringTest {

    
    @Test
    public void testJavaDocExample() {
        final ParsedString pr = new ParsedString("foo bar test\\ well", Collections.singleton(' '), '\\', Collections.<Character, Character>emptyMap());;
        final Iterator<String> it = pr.iterator();
        Assert.assertEquals("foo", it.next());
        Assert.assertEquals("bar", it.next());
        Assert.assertEquals("test well", it.next());
        Assert.assertEquals(false, it.hasNext());
    }
    
    @Test
    public void testEmptyString() {
        final ParsedString pr = new ParsedString("");
        final Iterator<String> it = pr.iterator();
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("", it.next());
        Assert.assertEquals(false, it.hasNext());
    }
    
    @Test
    public void testSingleField() {
        final ParsedString pr = new ParsedString("a");
        final Iterator<String> it = pr.iterator();
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("a", it.next());
        Assert.assertEquals(false, it.hasNext());
    }
    
    @Test
    public void testEscapedDelimiter() {
        final ParsedString pr = new ParsedString("foo\\;bar");
        final Iterator<String> it = pr.iterator();
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("foo;bar", it.next());
        Assert.assertEquals(false, it.hasNext());
    }
    
    @Test
    public void testEscapedEscape() {
        final ParsedString pr = new ParsedString("fooobaaa\\\\rrrr");
        final Iterator<String> it = pr.iterator();
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("fooobaaa\\rrrr", it.next());
        Assert.assertEquals(false, it.hasNext());
    }
    
    @Test(expected=IllegalStateException.class)
    public void testIllegalEscape() {
        final ParsedString pr = new ParsedString("a\\bc");
        final Iterator<String> it = pr.iterator();
        it.next();
    }
    
    @Test(expected=NoSuchElementException.class)
    public void testExhausted() {
        final ParsedString pr = new ParsedString("");
        final Iterator<String> it = pr.iterator();
        it.next();
        it.next();
    }
    
    @Test
    public void testSecondField() {
        final ParsedString pr = new ParsedString("foo;bar");
        final Iterator<String> it = pr.iterator();
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("foo", it.next());
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("bar", it.next());
        Assert.assertEquals(false, it.hasNext());
    }
    
    @Test
    public void testEmptySecondField() {
        final ParsedString pr = new ParsedString("foo;");
        final Iterator<String> it = pr.iterator();
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("foo", it.next());
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("", it.next());
        Assert.assertEquals(false, it.hasNext());
    }
    
    @Test
    public void testEmptyFirstField() {
        final ParsedString pr = new ParsedString(";bar");
        final Iterator<String> it = pr.iterator();
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("", it.next());
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("bar", it.next());
        Assert.assertEquals(false, it.hasNext());
    }
    
    @Test
    public void testEmptyFields() {
        final ParsedString pr = new ParsedString(";;;");
        final Iterator<String> it = pr.iterator();
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("", it.next());
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("", it.next());
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("", it.next());
        Assert.assertEquals(true, it.hasNext());
        Assert.assertEquals("", it.next());
        Assert.assertEquals(false, it.hasNext());
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void testCorrectExceptionRemove() {
        final ParsedString pr = new ParsedString(";;");
        final Iterator<String> it = pr.iterator();
        it.remove();
    }
    
    @Test
    public void testUnescapeNull() {
        final ParsedString pr = new ParsedString(";\\0;");
        final Iterator<String> it = pr.iterator();
        Assert.assertEquals("", it.next());
        Assert.assertEquals(null, it.next());
        Assert.assertEquals("", it.next());
        Assert.assertEquals(false, it.hasNext());
    }
    
    @Test
    public void testUnescapeNull2() {
        final ParsedString pr = new ParsedString(";\\0;a");
        final Iterator<String> it = pr.iterator();
        Assert.assertEquals("", it.next());
        Assert.assertEquals(null, it.next());
        Assert.assertEquals("a", it.next());
        Assert.assertEquals(false, it.hasNext());
    }
    
    @Test
    public void testTwoSeparators() {
        final ParsedString pr = new ParsedString(";foo=bar;a");
        final Iterator<String> it = pr.iterator();
        Assert.assertEquals("", it.next());
        Assert.assertEquals("foo", it.next());
        Assert.assertEquals("bar", it.next());
        Assert.assertEquals("a", it.next());
        Assert.assertEquals(false, it.hasNext());
    }
    
    @Test
    public void testEscapeEquals() {
        final ParsedString pr = new ParsedString(";foo\\=bar;a");
        final Iterator<String> it = pr.iterator();
        Assert.assertEquals("", it.next());
        Assert.assertEquals("foo=bar", it.next());
        Assert.assertEquals("a", it.next());
        Assert.assertEquals(false, it.hasNext());
    }
    
    @Test(expected=IllegalStateException.class)
    public void testIllegalNullEscapeInsideString() {
        final ParsedString pr = new ParsedString("a\\0");
        final Iterator<String> it = pr.iterator();
        it.next();
    }
    
    @Test(expected=IllegalStateException.class)
    public void testIllegalNullEscapeInsideString2() {
        final ParsedString pr = new ParsedString("\\0b");
        final Iterator<String> it = pr.iterator();
        it.next();
    }
}
