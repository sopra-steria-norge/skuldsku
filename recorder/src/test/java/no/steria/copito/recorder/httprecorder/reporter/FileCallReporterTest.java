package no.steria.copito.recorder.httprecorder.reporter;

import no.steria.copito.recorder.httprecorder.ReportObject;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class FileCallReporterTest {

    @Test
    public void shouldReturnRecordedDataAsString() throws FileNotFoundException {
        FileCallReporter fileCallReporter = FileCallReporter.create("FileCallReporterTest");
        ReportObject reportObject1 = new ReportObject().setMethod("POST").setOutput("Output").setPath("Path");
        ReportObject reportObject2 = new ReportObject().setMethod("GET").setOutput("Some output").setPath("index.html");

        fileCallReporter.reportCall(reportObject1);
        fileCallReporter.reportCall(reportObject2);
        String recordedData = fileCallReporter.getRecordedData();
        assertEquals("<no.steria.copito.recorder.httprecorder.ReportObject;readInputStream=<null>;parameters=<map>;method=POST;path=Path;output=Output;headers=<null>>\n" +
                "<no.steria.copito.recorder.httprecorder.ReportObject;readInputStream=<null>;parameters=<map>;method=GET;path=index.html;output=Some output;headers=<null>>", recordedData);
    }


}
