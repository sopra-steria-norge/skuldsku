package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RecordedDataMock implements MockInterface, Serializable {
    private final List<RecordObject> recorded;
    private String serviceClass;

    // Receives a list of recordings to be played back
    public RecordedDataMock(List<RecordObject> recorded) {
        this.recorded = recorded != null ? recorded : new ArrayList<RecordObject>();

    }

    @Override
    public Object invoke(Class<?> interfaceClass, Object serviceObject, Method method, Object[] args) {
        ClassSerializer serializer = new ClassSerializer();
        StringBuilder argsAsString = new StringBuilder();

        if (args != null) {
            for (Object obj : args) {
                argsAsString.append(serializer.asString(obj));
                argsAsString.append(";");
            }
            argsAsString.delete(argsAsString.length() - 1, argsAsString.length());
        }

        for (RecordObject recordObject : recorded) {
            if (serviceObject.getClass().getName().equals(recordObject.getServiceName())
                    && method.getName().equals(recordObject.getMethod())
                    && argsAsString.toString().equals(recordObject.getParameters())
                    ) {
                return serializer.asObject(recordObject.getResult());
            }
        }
        return null;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }
}
