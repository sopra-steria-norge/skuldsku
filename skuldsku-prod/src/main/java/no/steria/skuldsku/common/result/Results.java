package no.steria.skuldsku.common.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public final class Results implements Iterable<Result> {

    private final List<Result> results = new ArrayList<Result>();
    
    
    public Results() {
        
    }
    
    
    public void addResult(Result result) {
        results.add(result);
    }

    public List<Result> getResults() {
        return Collections.unmodifiableList(results);
    }
    
    @SuppressWarnings("unchecked")
    public <T  extends Result> List<T> getByType(Class<T> clazz) {
        final List<T> filtredResults = new ArrayList<T>();
        for (Result r : results) {
            if (clazz.isAssignableFrom(r.getClass())) {
                filtredResults.add((T) r);
            }
        }
        return filtredResults;
    }
    
    public boolean hasErrors() {
        for (Result r : results) {
            if (r.isFailure()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Result> iterator() {
        return Collections.unmodifiableList(results).iterator();
    }

    public static Results combine(Results... results) {
        final Results combinedResults = new Results();
        for (Results res : results) {
            combinedResults.results.addAll(res.results);
        }
        return combinedResults;
    }
}
