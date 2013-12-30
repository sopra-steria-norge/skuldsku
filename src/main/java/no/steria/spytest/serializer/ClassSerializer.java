package no.steria.spytest.serializer;

public class ClassSerializer {
    public String asString(Object object) {
        String classname = object.getClass().getName();
        return "<" + classname + ">";
    }
}
