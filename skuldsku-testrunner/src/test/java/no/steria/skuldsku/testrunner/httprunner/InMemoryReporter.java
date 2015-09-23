package no.steria.skuldsku.testrunner.httprunner;

import no.steria.skuldsku.recorder.http.HttpCall;
import no.steria.skuldsku.recorder.http.HttpCallPersister;

import java.util.ArrayList;
import java.util.List;

public class InMemoryReporter implements HttpCallPersister {
    private final List<HttpCall> playBook = new ArrayList<>();

    @Override
    public void initialize() {/* No initialization necessary. */}

    @Override
    public void reportCall(HttpCall reportObject) {
        playBook.add(reportObject);
    }

    public List<HttpCall> getPlayBook() {
        return playBook;
    }
}
