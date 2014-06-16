package no.steria.copito.testrunner.httprunner;

import no.steria.copito.recorder.httprecorder.CallReporter;
import no.steria.copito.recorder.httprecorder.ReportObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryReporter implements CallReporter {
    private final List<ReportObject> playBook = new ArrayList<>();

    @Override
    public void reportCall(ReportObject reportObject) {
        playBook.add(reportObject);
    }

    @Override
    public String getRecordedData() throws FileNotFoundException {
        throw new NotImplementedException();
    }

    public List<ReportObject> getPlayBook() {
        return playBook;
    }
}
