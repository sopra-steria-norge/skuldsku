package no.steria.copito.httpplayer;

import no.steria.copito.httpspy.CallReporter;
import no.steria.copito.httpspy.ReportObject;

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
