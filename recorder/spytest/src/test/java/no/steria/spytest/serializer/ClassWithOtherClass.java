package no.steria.spytest.serializer;

import java.util.List;

public class ClassWithOtherClass {
    private List<ClassWithSimpleFields> myProperty;

    public List<ClassWithSimpleFields> getMyProperty() {
        return myProperty;
    }

    public ClassWithOtherClass setMyProperty(List<ClassWithSimpleFields> myProperty) {
        this.myProperty = myProperty;
        return this;
    }
}
