package no.steria.httpplayer;

import jdk.internal.org.xml.sax.Attributes;
import no.steria.httpspy.ReportObject;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

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
    }

    private String recordField;

    public String getRecordField() {
        return recordField;
    }

    public String replacement() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser;
        try {
            saxParser = factory.newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
        ByteArrayInputStream is = new ByteArrayInputStream(recorded.getBytes());

        Handler dh = new Handler(recordField);
        try {
            saxParser.parse(is,dh);
        } catch (ParsingAborted e) {
           return dh.getValue();
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void record(String output) {
        this.recorded = output;
    }

    private static class ParsingAborted extends SAXException {

    }

    private static class Handler extends DefaultHandler {
        private String key;
        private String value;

        private Handler(String key) {
            this.key = key;
        }

        @Override
        public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
            if (!"input".equals(qName)) {
                return;
            }
            String name = attributes.getValue("name");
            if (!key.equals(name)) {
                return;
            }
            value = attributes.getValue("value");
            throw new ParsingAborted();
        }

        public String getValue() {
            return value;
        }
    }
}
