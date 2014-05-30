package no.steria.spytest.spy;

import no.steria.spytest.serializer.ClassWithSimpleFields;

import java.util.List;

public interface ServiceInterface {
    public String doSimpleService(String input);
    public List<String> returnList(ClassWithSimpleFields simple);
}
