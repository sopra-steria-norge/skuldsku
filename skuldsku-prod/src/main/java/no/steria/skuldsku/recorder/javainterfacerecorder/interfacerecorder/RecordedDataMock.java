package no.steria.skuldsku.recorder.javainterfacerecorder.interfacerecorder;

import no.steria.skuldsku.recorder.javainterfacerecorder.serializer.ClassSerializer;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RecordedDataMock implements MockInterface, Serializable {
    private final List<JavaInterfaceCall> recorded;
    private String serviceClass;

    // Receives a list of recordings to be played back
    public RecordedDataMock(List<JavaInterfaceCall> recorded) {
        this.recorded = recorded != null ? recorded : new ArrayList<JavaInterfaceCall>();

    }

    public String getImplementation() {
        return recorded.get(0).getClassName();
    }

    @Override
    public Object invoke(Class<?> interfaceClass, String serviceObjectName, Method method, Object[] args) {
        ClassSerializer serializer = new ClassSerializer();
        String argsAsString = "";
        if (args != null) {
            argsAsString = arrayToString(args, serializer);
        }

        for (JavaInterfaceCall recordObject : recorded) {
            if (
                    /* TODO: FIX (choose correct subclass+method combination):
                    serviceObjectName.equals(recordObject.getClassName())
                    &&
                     */
                    method.getName().equals(recordObject.getMethodname())
                    && argsAsString.equals(recordObject.getParameters())
                    ) {
                return serializer.asObject(recordObject.getResult());
            }
        }
        return null;
    }

    private String arrayToString(Object[] args, ClassSerializer serializer) {
        final StringBuilder argsAsString = new StringBuilder();
        for (Object obj : args) {
            argsAsString.append(serializer.asString(obj));
            argsAsString.append(";");
        }
        argsAsString.delete(argsAsString.length() - 1, argsAsString.length());
        return argsAsString.toString();
    }
    
    public Object[] stringToParameterArray(String parameters, ClassSerializer serializer) {
        final String[] a = parameters.split(";");
        final Object[] result = new Object[a.length];
        for (int i=0; i<a.length; i++) {
            result[i] = serializer.asObject(a[i]);
        }
        return result;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }
}
