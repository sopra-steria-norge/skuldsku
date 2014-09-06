package no.steria.skuldsku.testrunner.interfacerunner;

import java.util.ArrayList;
import java.util.List;

public class Pair<T> {
    private T a;
    private T b;

    public Pair(T a,T b) {
        this.a = a;
        this.b = b;
    }

    public static <T> List<Pair<T>> pairs(List<T> a,List<T> b) {
        if (a == null || b == null || a.size() != b.size()) {
            throw new IllegalArgumentException("Lists must be non null and have same length");
        }
        List<Pair<T>> result = new ArrayList<>();
        for (int i=0;i<a.size();i++) {
            result.add(new Pair<T>(a.get(i),b.get(i)));
        }
        return result;
    }

    public T getA() {
        return a;
    }

    public T getB() {
        return b;
    }
}
