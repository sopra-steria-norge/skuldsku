package no.steria.skuldsku.testrunner.dbrunner.dbchange;

public class FieldDifference {

    private final String name;
    private final String expectedValue;
    private final String actualValue;
    
    
    public FieldDifference(String name, String expectedValue, String actualValue) {
        this.name = name;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
    }
    
    
    public String getName() {
        return name;
    }
    
    public String getExpectedValue() {
        return expectedValue;
    }
    
    public String getActualValue() {
        return actualValue;
    }
    
}
