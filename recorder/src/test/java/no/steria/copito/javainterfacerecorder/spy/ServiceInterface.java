package no.steria.copito.javainterfacerecorder.spy;

import no.steria.copito.javainterfacerecorder.serializer.ClassWithSimpleFields;

import java.util.List;

public interface ServiceInterface {
    public String doSimpleService(String input);
    public String doWithPara(ServiceParameterClass para);
    public List<String> returnList(ClassWithSimpleFields simple);
}
