package no.steria.spytest.spy;

import no.steria.spytest.serializer.ClassWithSimpleFields;

import java.util.Arrays;
import java.util.List;

public class ServiceClass implements ServiceInterface {
    public String doSimpleService(String input) {
        return "Hello " + input;
    }

    public List<String> returnList(ClassWithSimpleFields simple) {
        if (simple == null) {
            return Arrays.asList("This","is","null");
        }
        return Arrays.asList("This","is","not","null");
    }
}
