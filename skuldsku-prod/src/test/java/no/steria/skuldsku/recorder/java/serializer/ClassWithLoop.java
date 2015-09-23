package no.steria.skuldsku.recorder.java.serializer;

public class ClassWithLoop {
    private String value;
    private ClassWithLoop other;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ClassWithLoop getOther() {
        return other;
    }

    public void setOther(ClassWithLoop other) {
        this.other = other;
    }
}
