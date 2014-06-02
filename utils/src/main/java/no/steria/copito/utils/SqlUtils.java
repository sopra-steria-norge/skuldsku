package no.steria.copito.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility methods for working with SQL
 */
public final class SqlUtils {

    private SqlUtils() {}
    
    /**
     * Reads an <code>SQL</code> statement.
     * @param filename The file to be reading the <code>SQL</code> from.
     * @return The <code>SQL</code> statement.
     */
    public static String readSql(String filename) {
        return readSql(filename, Collections.<String, String>emptyMap());
    }
    
    /**
     * Reads an <code>SQL</code> statement and performs a search-replace.
     * @param filename The file to be reading the <code>SQL</code> from.
     * @param replace A map of search-value to replace-value.
     * @return The <code>SQL</code> statement with the given replacements
     *          performed.
     */
    public static String readSql(String filename, Map<String, String> replace) {
        final InputStream is = SqlUtils.class.getResourceAsStream("/" + filename);
        if (is == null) {
            throw new IllegalStateException("Cannot find SQL-file: " + filename + " on classpath.");
        }
        final BufferedReader in = new BufferedReader(new InputStreamReader(is));
        try {
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                if (sb.length() != 0) {
                    sb.append('\n');
                }
                sb.append(replaceAll(line, replace));
            }
            return sb.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Error while reading SQL file: " + filename);
        } finally {
            try {
                in.close();
            } catch (IOException e) {}
        }
    }
    
    static String replaceAll(String s, Map<String, String> replace) {
        for (Entry<String, String> replaceEntry : replace.entrySet()) {
            s = s.replace(replaceEntry.getKey(), replaceEntry.getValue());
        }
        return s;
    }
}
