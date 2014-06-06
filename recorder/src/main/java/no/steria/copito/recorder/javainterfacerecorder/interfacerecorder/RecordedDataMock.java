package no.steria.copito.recorder.javainterfacerecorder.interfacerecorder;

import java.lang.reflect.Method;
import java.util.List;

public class RecordedDataMock implements MockInterface {
    private List<RecordObject> recorded;

    public RecordedDataMock(List<RecordObject> recorded) {
        this.recorded = recorded;
    }

    @Override
    public Object invoke(Class<?> interfaceClass, Object serviceObject, Method method, Object[] args) {
        return null;
    }
}
