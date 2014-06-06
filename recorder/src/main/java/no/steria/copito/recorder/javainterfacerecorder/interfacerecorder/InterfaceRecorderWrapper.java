package no.steria.copito.recorder.javainterfacerecorder.interfacerecorder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InterfaceRecorderWrapper implements java.lang.reflect.InvocationHandler {
    private static InterfaceRecorderConfig interfaceRecorderConfig;
    private Object obj;
    private final ReportCallback reportCallback;
    private Class<?> givenInterface;

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Object obj, Class<T> givenInterface, ReportCallback reportCallback, InterfaceRecorderConfig interfaceRecorderConfig) {
        InterfaceRecorderWrapper.interfaceRecorderConfig = interfaceRecorderConfig;
        InterfaceRecorderWrapper debugProxy = new InterfaceRecorderWrapper(obj,reportCallback,givenInterface);
        Object o = Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class<?>[]{givenInterface}, debugProxy);
        return (T) o;
    }

    private InterfaceRecorderWrapper(Object obj, ReportCallback reportCallback, Class<?> givenInterface) {
        this.obj = obj;
        this.reportCallback = reportCallback;
        this.givenInterface = givenInterface;
    }

    public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable {
        MockInterface mock = MockRegistration.getMock(givenInterface);
        Object result = null;
        try {
            if (mock != null) {
                result = mock.invoke(givenInterface,obj,m,args);
            } else {
                result = m.invoke(obj, args);
            }
            return result;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " +
                    e.getMessage());
        } finally {
            String className = obj.getClass().getName();
            String methodName = m.getName();
            LogRunner.log(reportCallback,className,methodName,args,result, interfaceRecorderConfig);

        }
    }
}
