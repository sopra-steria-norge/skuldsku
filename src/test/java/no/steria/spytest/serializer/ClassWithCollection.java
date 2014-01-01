package no.steria.spytest.serializer;

public class ClassWithCollection {
    private String arrVal[];

    public String[] getArrVal() {
        return arrVal;
    }

    public ClassWithCollection setArrVal(String[] arrVal) {
        this.arrVal = arrVal;
        return this;
    }
}
