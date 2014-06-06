package no.steria.copito.javainterfacerecorder.interfacerecorder;

import java.lang.reflect.Method;

public class IgnorePara {
    private Class<?> serviceClass;
    private Method serviceMethod;
    private Class<?> ignore;

    IgnorePara(Class<?> serviceClass, Method serviceMethod, Class<?> ignore) {
        this.serviceClass = serviceClass;
        this.serviceMethod = serviceMethod;
        this.ignore = ignore;
    }

    public Class<?> getServiceClass() {
        return serviceClass;
    }

    public Method getServiceMethod() {
        return serviceMethod;
    }

    public Class<?> getIgnore() {
        return ignore;
    }
}
