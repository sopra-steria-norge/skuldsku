package no.steria.skuldsku.testrunner.interfacerunner;

public class CompareResult {
    private boolean ok;

    public static CompareResult ok() {
        CompareResult compareResult = new CompareResult();
        compareResult.ok = true;
        return compareResult;
    }

    public static CompareResult fail() {
        CompareResult compareResult = new CompareResult();
        compareResult.ok = false;
        return compareResult;
    }

    public boolean isEqual() {
        return ok;
    }
}
