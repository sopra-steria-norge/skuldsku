package no.steria.spytest.serializer;

public class ClassWithSimpleFields {
    private String stringval;
    private int intval;

    public String getStringval() {
        return stringval;
    }

    public ClassWithSimpleFields setStringval(String stringval) {
        this.stringval = stringval;
        return this;
    }

    public int getIntval() {
        return intval;
    }

    public ClassWithSimpleFields setIntval(int intval) {
        this.intval = intval;
        return this;
    }
}
