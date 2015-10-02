package no.steria.skuldsku.testrunner.resulthandler;

import no.steria.skuldsku.common.result.Result;
import no.steria.skuldsku.common.result.Results;


public class ConsoleResultHandler implements ResultHandler {

    @Override
    public void handle(Results results) {
        for (Result result : results) {
            System.out.println(result.toString());
        }
    }

}
