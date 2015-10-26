package no.steria.skuldsku.testrunner.httprunner.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HttpCallVerifierOptions {

    private final List<String> outputComparisionIgnores;
    private final Set<String> comparisionSkipPaths; 
    
    public HttpCallVerifierOptions() {
        outputComparisionIgnores = new ArrayList<>();
        comparisionSkipPaths = new HashSet<>();
    }
    
    public HttpCallVerifierOptions(HttpCallVerifierOptions options) {
        this.outputComparisionIgnores = new ArrayList<String>(options.outputComparisionIgnores);
        this.comparisionSkipPaths = new HashSet<String>(options.comparisionSkipPaths);
    }
    
    
    public void addOuputComparisionIgnore(String outputComparisionIgnoreRegex) {
        outputComparisionIgnores.add(outputComparisionIgnoreRegex);
    }
    
    public List<String> getOutputComparisionIgnores() {
        return Collections.unmodifiableList(outputComparisionIgnores);
    }
    
    public void addComparisionSkipPaths(String comparisionSkipPath) {
        comparisionSkipPaths.add(comparisionSkipPath);
    }
    
    public Set<String> getComparisionSkipPaths() {
        return Collections.unmodifiableSet(comparisionSkipPaths);
    }
}
