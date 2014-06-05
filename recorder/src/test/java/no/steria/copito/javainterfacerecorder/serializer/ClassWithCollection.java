package no.steria.copito.javainterfacerecorder.serializer;

import java.util.List;

public class ClassWithCollection {
    private String arrVal[];
    private List<Integer> numbers;

    public String[] getArrVal() {
        return arrVal;
    }

    public ClassWithCollection setArrVal(String[] arrVal) {
        this.arrVal = arrVal;
        return this;
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public ClassWithCollection setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
        return this;
    }
}
