package no.steria.copito.recorder.httprecorder.testjetty;

import no.steria.copito.recorder.httprecorder.ReportObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class InMemoryReporterTest {

    @Test
    public void shouldCreateStringFromPlayBooks() {
        InMemoryReporter inMemoryReporter = new InMemoryReporter();
        inMemoryReporter.reportCall(new ReportObject().setMethod("POST").setPath("http://path.com"));
        inMemoryReporter.reportCall(new ReportObject().setMethod("GET").setPath("http://another.path.com"));
        assertEquals("<no.steria.copito.recorder.httprecorder.ReportObject;readInputStream=<null>;parameters=<map>;" +
                "method=POST;path=http://path.com;output=<null>;headers=<null>><no.steria.copito.recorder." +
                "httprecorder.ReportObject;readInputStream=<null>;parameters=<map>;method=GET;path=http://another." +
                "path.com;output=<null>;headers=<null>>", inMemoryReporter.getRecordedData());
    }
}
