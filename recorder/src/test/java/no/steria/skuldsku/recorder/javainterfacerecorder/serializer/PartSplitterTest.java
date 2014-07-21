package no.steria.skuldsku.recorder.javainterfacerecorder.serializer;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class PartSplitterTest {
    private final ClassSerializer classSerializer = new ClassSerializer();

    @Test
    public void shouldSplitBasic() throws Exception {
        String[] parts = classSerializer.splitToParts("<a;b;c>");
        assertThat(parts).containsOnly("a","b","c");
    }

    @Test
    public void shouldSplitWithSpesialCharacters() throws Exception {
        String[] parts = classSerializer.splitToParts("<a;<b>;c>");
        assertThat(parts).containsOnly("a","<b>","c");
    }

    @Test
    public void shouldIgnoreInnerSplits() throws Exception {
        String[] parts = classSerializer.splitToParts("<a;<b;d;e>;f>");
        assertThat(parts).containsOnly("a","<b;d;e>","f");
    }
}
