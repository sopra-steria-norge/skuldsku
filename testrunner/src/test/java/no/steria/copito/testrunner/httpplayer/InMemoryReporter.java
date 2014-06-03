package no.steria.copito.testrunner.httpplayer;

import no.steria.copito.testrunner.httpspy.CallReporter;
import no.steria.copito.testrunner.httpspy.ReportObject;

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
