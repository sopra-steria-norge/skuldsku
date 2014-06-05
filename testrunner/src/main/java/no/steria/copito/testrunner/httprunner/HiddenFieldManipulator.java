package no.steria.copito.testrunner.httprunner;

import java.util.HashMap;
import java.util.Map;

public class HiddenFieldManipulator implements PlaybackManipulator {
    private final String fieldname;

    private String value;

    public HiddenFieldManipulator(String fieldname) {
        this.fieldname = fieldname;
    }

    @Override
    public String computePayload(String readInputStream) {
        if (readInputStream == null) {
            return null;
        }
        String field = fieldname;
        String replacementVal = value;


        StringBuilder inpStr = new StringBuilder(readInputStream);
        String searchStr = field + "=";
        String replacement = field + "=" + replacementVal;
        for (int pos = inpStr.indexOf(searchStr);pos != -1;pos = inpStr.indexOf(searchStr,pos+1)) {
            int endpos = inpStr.indexOf("&",pos);
            if (endpos == -1) {
                endpos = inpStr.length();
            }

            inpStr.replace(pos,endpos,replacement);
        }
        return inpStr.toString();
    }

    @Override
    public void reportResult(String recorded) {
        String replacement = replacement(recorded);
        if (replacement != null) {
            value = replacement;
        }
    }

    public String replacement(String recorded) {
        if (recorded == null) {
            return null;
        }
        for (int pos = recorded.indexOf("<input"); pos != -1; pos = recorded.indexOf("<input", pos + 1)) {
            int endpos = recorded.indexOf(">", pos);
            if (endpos == -1) {
                continue;
            }
            String element = recorded.substring(pos, endpos + 1);
            Map<String, String> attributes = readAttributes(element);
            if (!fieldname.equals(attributes.get("name"))) {
                continue;
            }
            return attributes.get("value");
        }
        return null;
    }

    private static enum ParseState {
        LOOKING_FOR_ATTRIBUTE_NAME_START, IN_ATTRIBUTE_NAME, LOOKING_FOR_VALUE, IN_VALUE;
    }

    private Map<String, String> readAttributes(String element) {
        Map<String, String> attributes = new HashMap<>();
        ParseState state = ParseState.LOOKING_FOR_ATTRIBUTE_NAME_START;
        int start = -1;
        String name = null;
        for (int pos = 6; pos < element.length(); pos++) {
            char ch = element.charAt(pos);
            boolean whitespace = Character.isWhitespace(ch);

            switch (state) {
                case LOOKING_FOR_ATTRIBUTE_NAME_START:
                    if (!whitespace) {
                        start = pos;
                        state = ParseState.IN_ATTRIBUTE_NAME;
                    }
                    break;
                case IN_ATTRIBUTE_NAME:
                    if (whitespace || ch == '=') {
                        name = element.substring(start, pos);
                        state = ParseState.LOOKING_FOR_VALUE;
                    }
                    break;
                case LOOKING_FOR_VALUE:
                    if (ch == '"' || ch == '\'') {
                        start = pos + 1;
                        state = ParseState.IN_VALUE;
                    }
                    break;
                case IN_VALUE:
                    if (ch == '"' || ch == '\'') {
                        String value = element.substring(start, pos);
                        attributes.put(name, value);
                        state = ParseState.LOOKING_FOR_ATTRIBUTE_NAME_START;
                    }
                    break;
            }
        }
        return attributes;
    }
}
