package no.steria.skuldsku.testrunner.httprunner.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HttpCallVerifierOptions {

    private final List<String> outputComparisionIgnores;
    private final Set<String> outputComparisionSkipPaths; 
    
    public HttpCallVerifierOptions() {
        outputComparisionIgnores = new ArrayList<>();
        outputComparisionSkipPaths = new HashSet<>();
    }
    
    public HttpCallVerifierOptions(HttpCallVerifierOptions options) {
        this.outputComparisionIgnores = new ArrayList<String>(options.outputComparisionIgnores);
        this.outputComparisionSkipPaths = new HashSet<String>(options.outputComparisionSkipPaths);
    }
    
    
    public void addOuputComparisionIgnore(String outputComparisionIgnoreRegex) {
        outputComparisionIgnores.add(outputComparisionIgnoreRegex);
    }
    
    public List<String> getOutputComparisionIgnores() {
        return Collections.unmodifiableList(outputComparisionIgnores);
    }
    
    public void addOutputComparisionSkipPaths(String outputComparisionSkipPath) {
        outputComparisionSkipPaths.add(outputComparisionSkipPath);
    }
    
    public Set<String> getOutputComparisionSkipPaths() {
        return Collections.unmodifiableSet(outputComparisionSkipPaths);
    }
}
