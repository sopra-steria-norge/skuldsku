package no.steria.copito.recorder.httprecorder.reporter;

import no.steria.copito.recorder.httprecorder.ReportObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

public class FileCallReporterTest {

    private File tempFile;

    @Before
    public void setUp() throws Exception {
        tempFile = File.createTempFile("FileCallReporterTest", "tmp");


    }

    @Test
    public void shouldReturnRecordedDataAsString() throws IOException {
        FileCallReporter fileCallReporter = FileCallReporter.create(tempFile);
        ReportObject reportObject1 = new ReportObject().setMethod("POST").setOutput("Output").setPath("Path");
        ReportObject reportObject2 = new ReportObject().setMethod("GET").setOutput("Some output").setPath("index.html");

        fileCallReporter.reportCall(reportObject1);
        fileCallReporter.reportCall(reportObject2);
        String recordedData = fileCallReporter.getRecordedData();
        assertEquals("<no.steria.copito.recorder.httprecorder.ReportObject;readInputStream=<null>;parameters=<map>;method=POST;path=Path;output=Output;headers=<null>>\n" +
                "<no.steria.copito.recorder.httprecorder.ReportObject;readInputStream=<null>;parameters=<map>;method=GET;path=index.html;output=Some output;headers=<null>>", recordedData);
    }

    @After
    public void tearDown() throws Exception {
        tempFile.delete();
    }
}
