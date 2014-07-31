package no.steria.skuldsku.testrunner.httprunner;

import au.com.bytecode.opencsv.CSVReader;
import no.steria.skuldsku.recorder.httprecorder.ReportObject;
import no.steria.skuldsku.testrunner.DbToFileExporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static no.steria.skuldsku.testrunner.DbToFileExporter.ANT_COLUMNS_HTTP_RECORDINGS;

public class StreamDbPlayBack {

    public void play(InputStream recordingStream, HttpPlayer httpPlayer) throws IOException {
        InputStreamReader in = new InputStreamReader(recordingStream);
        BufferedReader bufferedReader = new BufferedReader(in);
        httpPlayer.addManipulator(new HiddenFieldManipulator("oracle.adf.faces.STATE_TOKEN"));

        CSVReader reader = new CSVReader(bufferedReader, ',', '"');
        String[] nextLine;
        String[] next = reader.readNext();
        while(!next[0].equals(DbToFileExporter.HTTP_RECORDINGS_HEADER)){
            next = reader.readNext();
        }
        while ((nextLine = reader.readNext()) != null) {
            if (nextLine.length == ANT_COLUMNS_HTTP_RECORDINGS) {
                ReportObject reportObject = ReportObject.parseFromString(nextLine[ANT_COLUMNS_HTTP_RECORDINGS -1]);
                httpPlayer.playStep(new PlayStep(reportObject));
            }
        }
    }
}
