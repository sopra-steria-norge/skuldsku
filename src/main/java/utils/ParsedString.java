package utils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A record parser.
 */
public class ParsedString implements Iterable<String> {

    private final String record;
    private final char delimiterChar;
    private final char escapeChar;
    
    private final Map<Character, Character> escapes;
    
    
    /**
     * Creates a parser for the given <code>String</code>
     * @param s The <code>String</code> to be parsed using ";" as a delimiter between the records
     *          and "\" as the escape sign.
     */
    public ParsedString(String s) {
        this(s, ';', '\\', getDefaultEscapes());
    }
    
    ParsedString(String record, char delimiter, char escape, Map<Character, Character> escapes) {
        this.record = record;
        this.delimiterChar = delimiter;
        this.escapeChar = escape;
        this.escapes = escapes;
        
        this.escapes.put(delimiterChar, delimiterChar);
        this.escapes.put(escapeChar, escapeChar);
    }
    
    
    private static Map<Character, Character> getDefaultEscapes() {
        final Map<Character, Character> escapes =new HashMap<Character, Character>();
        escapes.put('0', null);
        return escapes;
    }
    
    /**
     * Returns an <code>Iterator</code> on the tokens in this <code>ParsedString</code>.
     */
    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private int index = 0;
            private StringBuilder nextToken = new StringBuilder();
            private boolean escaped = false;
            
            @Override
            public boolean hasNext() {
                return index <= record.length();
            }

            @Override
            public String next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                while (index < record.length()) {
                    char c = record.charAt(index);
                    index++;
                    
                    if (escaped) {
                        /*
                         * Handle escapes:
                         */
                        if (!escapes.containsKey(c)) {
                            throw new IllegalStateException("Unknown escape character: " + c);
                        }

                        final Character escapeValue = escapes.get(c);
                        if (escapeValue == null) {
                            if (nextToken.length() != 0 || record.charAt(index) != delimiterChar) {
                                throw new IllegalStateException("Null can only be returned when there is no other data in the same field.");
                            }
                            index++;
                            return null;
                        }
                        nextToken.append(escapeValue);
                        escaped = false;
                    } else if (c == delimiterChar) {
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
