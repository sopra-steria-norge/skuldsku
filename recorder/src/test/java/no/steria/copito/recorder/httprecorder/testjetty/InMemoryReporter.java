package no.steria.copito.recorder.httprecorder.testjetty;

import no.steria.copito.recorder.httprecorder.CallReporter;
import no.steria.copito.recorder.httprecorder.ReportObject;

import java.util.ArrayList;
import java.util.List;

public class InMemoryReporter implements CallReporter {
    private final List<ReportObject> playBook = new ArrayList<>();

    @Override
    public void reportCall(ReportObject reportObject) {
        playBook.add(reportObject);
    }

    @Override
    public String getRecordedData() {
        StringBuilder reportBuilder = new StringBuilder();
        for(ReportObject report : playBook) {
            reportBuilder.append(report.serializedString());
        }
        return reportBuilder.toString();
    }

    public List<ReportObject> getPlayBook() {
        return playBook;
    }
}
