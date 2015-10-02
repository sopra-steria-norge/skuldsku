package no.steria.skuldsku.testrunner.resulthandler;

import no.steria.skuldsku.common.result.Result;
import no.steria.skuldsku.common.result.Results;


public class ExceptionResultHandler implements ResultHandler {

    @Override
    public void handle(Results results) {
        for (Result t : results) {
            throw new IllegalStateException(t.toString());
        }
    }
    

}
