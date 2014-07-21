package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassWithSimpleFields;

import java.io.File;
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

    @Override
    public String readAFile(String prefix, File file) {
        return null;
    }
}
