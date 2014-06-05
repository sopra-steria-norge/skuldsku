package no.steria.copito.httprecorder.testjetty;

import no.steria.copito.httprecorder.CallReporter;
import no.steria.copito.httprecorder.ReportObject;

import java.util.ArrayList;
import java.util.List;

public class InMemoryReporter implements CallReporter {
    private List<ReportObject> playBook = new ArrayList<>();

    @Override
    public void reportCall(ReportObject reportObject) {
        playBook.add(reportObject);
    }

    public List<ReportObject> getPlayBook() {
        return playBook;
    }
}
