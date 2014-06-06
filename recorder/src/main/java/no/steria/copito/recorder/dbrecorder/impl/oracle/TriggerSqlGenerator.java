package no.steria.copito.recorder.dbrecorder.impl.oracle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.steria.copito.utils.SqlUtils;

/**
 * Implements the dynamic generation of a recording trigger.
 * 
 * @see #generateTriggerSql(String, String, List)
 */
final class TriggerSqlGenerator {
    
    private TriggerSqlGenerator() {}
    
    static String generateTriggerSql(String triggerName, String tableName, List<String> columns) {
        return generateTriggerSql(triggerName,
                tableName,
                generateInsertDataRow(columns),
                generateUpdateDataRow(columns),
                generateDeleteDataRow(columns));
    }
    
    static String generateInsertDataRow(List<String> columns) {
        final StringBuilder dataRow = new StringBuilder();
        
        boolean needsConcat = false;
        for (String column : columns) {
            if (needsConcat) {
                dataRow.append("||';'||");
            } else {
                needsConcat = true;
            }
            dataRow.append(columnNameAndValue(column, "ROW", ":new."));
        }
        
        return dataRow.toString();
    }
    
    static String generateUpdateDataRow(List<String> columns) {
        final StringBuilder dataRow = new StringBuilder();
        
        boolean needsConcat = false;
        for (String column : columns) {
            if (needsConcat) {
                dataRow.append("||';'||");
            } else {
                needsConcat = true;
            }
            dataRow.append(columnNameAndValue(column, "ROW", ":new."));
            dataRow.append("||';'||");
            dataRow.append(columnNameAndValue(column, "OLDROW", ":old."));
        }
        
        return dataRow.toString();
    }
    
    static String generateDeleteDataRow(List<String> columns) {
        final StringBuilder dataRow = new StringBuilder();
        
        boolean needsConcat = false;
        for (String column : columns) {
            if (needsConcat) {
                dataRow.append("||';'||");
            } else {
                needsConcat = true;
            }
            dataRow.append(columnNameAndValue(column, "ROW", ":old."));
        }
        
        return dataRow.toString();
    }

    static String columnNameAndValue(String column, String name, String prefix) {
        return "'" + name + "." + column + "='||" + escapeField(column, prefix);
    }
    
    static String escapeField(String field, String prefix) {
        return "NVL(REPLACE(REPLACE(REPLACE(" + prefix + field + ", '\\', '\\\\'), ';', '\\;'), '=', '\\='), '\\0')";
    }
    
    static String generateTriggerSql(String triggerName, String tableName, String insertDataRow, String updateDataRow, String deleteDataRow) {
        final Map<String, String> replace = new HashMap<String, String>();
        replace.put("::TRIGGER_NAME::", triggerName);
        replace.put("::TABLE_NAME::", tableName);
        replace.put("::INSERT_DATAROW::", insertDataRow);
        replace.put("::UPDATE_DATAROW::", updateDataRow);
        replace.put("::DELETE_DATAROW::", deleteDataRow);
        
        return SqlUtils.readSql("dbrecorder/trigger.sql", replace);
    }
}
