package no.steria.skuldsku.testrunner.dbrunner.dbchange;

import no.steria.skuldsku.recorder.logging.RecorderLog;
import no.steria.skuldsku.utils.ParsedString;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Represents a single database INSERT, UPDATE or DELETE operation.
 */
public class DatabaseChange {
    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String ACTION = "ACTION";
    
    private final String row;
    private final Map<String, String> data;
    private final int lineNumber;


    /**
     * Creates a <code>DatabaseChange</code>.
     * 
     * @param row The <code>String</code>, representing a database change, having the format
     *          <code>KEY=VALUE[;KEY=VALUE]*</code>.
     * @param lineNumber The line the string was taken from. This may be used by
     *          a <code>DatabaseChangeVerifier</code>.
     */
    public DatabaseChange(String row, int lineNumber) {
        if (row == null) {
            throw new NullPointerException("The String 'row' cannot be null.");
        }
        if (lineNumber <= 0) {
            throw new IllegalArgumentException("The int 'lineNumber' must be greater than zero.");
        }
        this.row = row;
        this.data = extractData(row);
        this.lineNumber = lineNumber;
    }


    static Map<String, String> extractData(String row) {
        final Map<String, String> data = new LinkedHashMap<>();
        
        final Iterator<String> it = new ParsedString(row).iterator();
        while (it.hasNext()) {
            final String key = it.next();
            final String value = it.next();
            data.put(key, value);
        }
        return Collections.unmodifiableMap(data);
    }
    
    public int getLineNumber() {
        return lineNumber;
    }

    public Map<String, String> getData() {
        return data;
    }
    
    public String getValue(String key) {
        return data.get(key);
    }
    
    public String getTableName() {
        return data.get(TABLE_NAME);
    }
    
    public String getAction() {
        return data.get(ACTION);
    }
    
    List<Entry<String, String>> getFields(String start) {
        return data.entrySet().stream().filter(entry -> entry.getKey().startsWith(start)).collect(Collectors.toList());
    }
    
    public int fieldsMatched(DatabaseChange databaseChange, Set<String> skipFields) {
        int result = 0;
        for (String key : data.keySet()) {
            if (skipFields.contains(key)) {
                continue;
            }
            final String v1 = data.get(key);
            final String v2 = databaseChange.data.get(key);
            if ((v1 != null && v2!= null && v1.equals(v2)) || (v1 == null && v2 == null)) {
                result++;
            }
        }
        return result;
    }
    
    public boolean equals(DatabaseChange databaseChange, Set<String> skipFields) {
        for (String key : data.keySet()) {
            if (skipFields.contains(key)) {
                continue;
            }
            if (key.startsWith("OLDROW")) {
                continue;
            }
            final String v1 = data.get(key);
            final String v2 = databaseChange.data.get(key);
            if ((v1 != null && v2!= null && !v1.equals(v2)) || (v1 == null ^ v2 == null)) {
                return false;
            }
        }
        return true;
    }

    public static List<DatabaseChange> readDatabaseChanges(File f) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(f));
            return readDatabaseChanges(in);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    RecorderLog.error("Could not close stream.", e);
                }
            }
        }
    }

    public static List<DatabaseChange> readDatabaseChanges(BufferedReader in) {
        try {
            final List<DatabaseChange> result = new ArrayList<>();
            String line;
            int lineNumber = 1;
            while ((line = in.readLine()) != null) {
                result.add(new DatabaseChange(line, lineNumber));
                lineNumber++;
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static List<DatabaseChange> toDatabaseChangeList(String[] rowArray) {
        final List<DatabaseChange> result = new ArrayList<>(rowArray.length);
        for (int index = 0; index<rowArray.length; index++) {
            result.add(new DatabaseChange(rowArray[index], index + 1));
        }
        return result;
    }
    
    @Override
    public String toString() {
        return row;
    }
}
