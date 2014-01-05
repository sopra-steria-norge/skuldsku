package no.steria.spytest.spy;

public class ServiceClass implements ServiceInterface {
    public String doSimpleService(String input) {
        return "Hello " + input;
    }
}
