package no.steria.spytest.serializer;

public class ClassSerializer {
    public String asString(Object object) {
        if (object == null) {
            return "<null>";
        }
        String classname = object.getClass().getName();
        return "<" + classname + ">";
    }
}
