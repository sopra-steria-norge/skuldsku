package no.steria.copito.javainterfacerecorder.spy;

import no.steria.copito.javainterfacerecorder.serializer.ClassWithSimpleFields;

import java.util.List;

public class ServiceMock implements ServiceInterface {
    @Override
    public String doSimpleService(String input) {
        return "I am the mock " + input;
    }

    @Override
    public String doWithPara(ServiceParameterClass para) {
        return null;
    }

    @Override
    public List<String> returnList(ClassWithSimpleFields simple) {
        return null;
    }
}
