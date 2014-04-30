package no.steria.httpplayer;

import no.steria.httpspy.ReportObject;

import java.util.HashMap;
import java.util.Map;

public class PlayStep {
    private ReportObject reportObject;
    private String recorded;

    public PlayStep(ReportObject reportObject) {
        this.reportObject = reportObject;
    }

    public ReportObject getReportObject() {
        return reportObject;
    }

    public void setReplacement(String parameterName, PlayStep copyFrom) {
        copyFrom.recordField = parameterName;
        this.copyFrom = copyFrom;
    }

    private String recordField;
    private PlayStep copyFrom;

    public String inputToSend() {
        String readInputStream = reportObject.getReadInputStream();
        if (copyFrom == null) {
            return readInputStream;
        }
        String field = copyFrom.recordField;
        String replacementVal = copyFrom.replacement();

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



    protected String replacement() {
        if (recorded == null) {
            return null;
        }
        for (int pos = recorded.indexOf("<input"); pos != -1; pos = recorded.indexOf("<input", pos + 1)) {
            int endpos=recorded.indexOf(">",pos);
            if (endpos == -1) {
                continue;
            }
            String element = recorded.substring(pos,endpos+1);
            Map<String,String> attributes=readAttributes(element);
            if (!recordField.equals(attributes.get("name"))) {
                continue;
            }
            return attributes.get("value");
        }
        return null;
    }

    private static enum ParseState {
        LOOKING_FOR_ATTRIBUTE_NAME_START,IN_ATTRIBUTE_NAME,LOOKING_FOR_VALUE,IN_VALUE;
    }

    private Map<String, String> readAttributes(String element) {
        Map<String,String> attributes=new HashMap<>();
        ParseState state = ParseState.LOOKING_FOR_ATTRIBUTE_NAME_START;
        int start=-1;
        String name=null;
        for (int pos=6;pos<element.length();pos++) {
            char ch = element.charAt(pos);
            boolean whitespace = Character.isWhitespace(ch);

            switch (state) {
                case LOOKING_FOR_ATTRIBUTE_NAME_START:
                    if (!whitespace) {
                        start=pos;
                        state=ParseState.IN_ATTRIBUTE_NAME;
                    }
                    break;
                case IN_ATTRIBUTE_NAME:
                    if (whitespace || ch == '=') {
                        name = element.substring(start,pos);
                        state = ParseState.LOOKING_FOR_VALUE;
                    }
                    break;
                case LOOKING_FOR_VALUE:
                    if (ch == '"' || ch == '\'') {
                        start = pos+1;
                        state = ParseState.IN_VALUE;
                    }
                    break;
                case IN_VALUE:
                    if (ch == '"' || ch == '\'') {
                        String value = element.substring(start,pos);
                        attributes.put(name,value);
                        state = ParseState.LOOKING_FOR_ATTRIBUTE_NAME_START;
                    }
                    break;
            }

        }
        return attributes;
    }


    public void record(String output) {
        this.recorded = output;
    }




    public String getRecorded() {
        return recorded;
    }
}
