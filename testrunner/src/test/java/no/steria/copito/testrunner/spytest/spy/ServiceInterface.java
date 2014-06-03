package no.steria.copito.testrunner.spytest.spy;

import no.steria.copito.testrunner.spytest.serializer.ClassWithSimpleFields;

import java.util.List;

public interface ServiceInterface {
    public String doSimpleService(String input);
    public String doWithPara(ServiceParameterClass para);
    public List<String> returnList(ClassWithSimpleFields simple);
}
