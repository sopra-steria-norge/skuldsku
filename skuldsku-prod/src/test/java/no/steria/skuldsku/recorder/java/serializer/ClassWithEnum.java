package no.steria.skuldsku.recorder.java.serializer;

public class ClassWithEnum {
    private DummyEnum myEnum;
    private String myText;

    public DummyEnum getMyEnum() {
        return myEnum;
    }

    public ClassWithEnum setMyEnum(DummyEnum myEnum) {
        this.myEnum = myEnum;
        return this;
    }

    public String getMyText() {
        return myText;
    }

    public ClassWithEnum setMyText(String myText) {
        this.myText = myText;
        return this;
    }
}
