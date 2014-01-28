package utils;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A record parser.
 */
public class ParsedString implements Iterable<String> {

    private final String record;
    private final Set<Character> delimiterChars;
    private final char escapeChar;
    
    private final Map<Character, Character> escapes;
    
    
    /**
     * Creates a parser for the given <code>String</code>
     * @param s The <code>String</code> to be parsed using ";" as a delimiter between the records
     *          and "\" as the escape sign.
     */
    public ParsedString(String s) {
        this(s, getDefaultDelimiters(), '\\', getDefaultEscapes());
    }
    
    /**
     * Creates a parser.
     * 
     * For example:
     * <p>
     *     <code>new ParsedString("foo bar test\\ well", Collections.singleton(' '), '\\', Collections.<Character, Character>emptyMap());</code>
     * </p>
     * <p>
     *     Results in the following fields when iterating:
     *     <ol>
     *       <li>foo</li>
     *       <li>bar</li>
     *       <li>test well</li>
     *     </ol>
     * </p>
     * 
     * @param record The record to be parsed.
     * @param delimiters A <code>Set</code> of delimiters used for separating fields
     *          in the given record. 
     * @param escape The escape character.
     * @param escapes A <code>Map</code> with mappings between escape characters and
     *          their values.
     */
    ParsedString(String record, Set<Character> delimiters, char escape, Map<Character, Character> escapes) {
        this.record = record;
        this.delimiterChars = delimiters;
        this.escapeChar = escape;
        this.escapes = new HashMap<Character, Character>(escapes);
        
        for (Character c : delimiterChars) {
            this.escapes.put(c, c);
        }
        this.escapes.put(escapeChar, escapeChar);
    }
    
    
    private static Map<Character, Character> getDefaultEscapes() {
        final Map<Character, Character> escapes =new HashMap<Character, Character>();
        escapes.put('0', null);
        return escapes;
    }
    
    private static Set<Character> getDefaultDelimiters() {
        final Set<Character> delimiters = new HashSet<Character>();
        delimiters.add(';');
        delimiters.add('=');
        return delimiters;
    }
    
    /**
     * Returns an <code>Iterator</code> on the tokens in this <code>ParsedString</code>.
     */
    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private int index = 0;
            private StringBuilder nextToken = new StringBuilder();
            
            @Override
            public boolean hasNext() {
                return index <= record.length();
            }

            @Override
            public String next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                boolean escaped = false;
                while (index < record.length()) {
                    char c = record.charAt(index);
                    index++;
                    
                    if (escaped) {
                        /*
                         * Handle escapes:
                         */
                        if (!escapes.containsKey(c)) {
                            throw new IllegalStateException("Unknown escape character: " + c);
                        } else {
                            final Character escapeValue = escapes.get(c);
                            if (escapeValue == null) {
                                if (nextToken.length() != 0 || !delimiterChars.contains(record.charAt(index))) {
                                    throw new IllegalStateException("Null can only be returned when there is no other data in the same field. Index=" + index + " String: " + record);
                                }
                                index++;
                                return null;
                            }
                            nextToken.append(escapeValue);
                            escaped = false;
                        }
                    } else if (delimiterChars.contains(c)) {
                        /*
                         * Field completely read:
                         */
                        final String s = nextToken.toString();
                        nextToken = new StringBuilder();
                        return s;
                    } else if (c == escapeChar) {
                        /*
                         * Next character is escaped:
                         */
                        escaped = true;
                    } else {
                        nextToken.append(c);
                    }
                }
                index++;
                return nextToken.toString();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove not supported");
            }
            
        };
    }
}
