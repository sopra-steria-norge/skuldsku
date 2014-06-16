package no.steria.copito.recorder.javainterfacerecorder.interfacerecorder;

import no.steria.copito.recorder.javainterfacerecorder.serializer.ClassWithSimpleFields;

import java.util.Arrays;
import java.util.List;

public class ServiceClass implements ServiceInterface {
    public String doSimpleService(String input) {
        return "Hello " + input;
    }

    @Override
    public String doWithPara(ServiceParameterClass para) {
        if (para == null) {
            return null;
        }
        return para.getInfo();
    }

    public List<String> returnList(ClassWithSimpleFields simple) {
        if (simple == null) {
            return Arrays.asList("This","is","null");
        }
        return Arrays.asList("This","is","not","null");
    }
}