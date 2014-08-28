package no.steria.skuldsku.testrunner.httprunner;

import au.com.bytecode.opencsv.CSVReader;
import no.steria.skuldsku.recorder.httprecorder.ReportObject;

import java.io.IOException;

import static no.steria.skuldsku.testrunner.DbToFileExporter.ANT_COLUMNS_HTTP_RECORDINGS;

public class StreamHttpPlayBack {

    public void play(CSVReader reader, HttpPlayer httpPlayer) throws IOException {

        httpPlayer.addManipulator(new HiddenFieldManipulator("oracle.adf.faces.STATE_TOKEN"));

        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            if (nextLine.length == ANT_COLUMNS_HTTP_RECORDINGS) {
                ReportObject reportObject = ReportObject.parseFromString(nextLine[ANT_COLUMNS_HTTP_RECORDINGS -1]);
                httpPlayer.playStep(new PlayStep(reportObject));
            }
        }
    }
}
