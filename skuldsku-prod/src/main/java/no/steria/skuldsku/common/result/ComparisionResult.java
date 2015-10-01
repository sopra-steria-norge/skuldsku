package no.steria.skuldsku.common.result;

public interface ComparisionResult<T> extends Result {

    public T getExpected();
    public T getActual();
    
}
