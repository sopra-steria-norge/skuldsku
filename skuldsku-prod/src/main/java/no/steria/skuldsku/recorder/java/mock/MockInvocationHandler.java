package no.steria.skuldsku.recorder.java.mock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MockInvocationHandler implements InvocationHandler {
    private Class interfaceClass;
    private MockInterface mi;

    public MockInvocationHandler(MockInterface mi, Class interfaceClass) {
        this.interfaceClass = interfaceClass;
        this.mi = mi;
    }

    public String getImplementationClass() {
        return ((RecordedDataMock)mi).getImplementation();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return mi.invoke(interfaceClass, interfaceClass.getName(), method, args);
    }
}

