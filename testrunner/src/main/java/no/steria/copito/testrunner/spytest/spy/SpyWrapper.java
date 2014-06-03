package no.steria.copito.testrunner.spytest.spy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SpyWrapper implements java.lang.reflect.InvocationHandler {
    private static SpyConfig spyConfig;
    private Object obj;
    private final ReportCallback reportCallback;

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Object obj, Class<T> givenInterface, ReportCallback reportCallback, SpyConfig spyConfig) {
        SpyWrapper.spyConfig = spyConfig;
        SpyWrapper debugProxy = new SpyWrapper(obj,reportCallback);
        Object o = Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class<?>[]{givenInterface}, debugProxy);
        return (T) o;
    }

    private SpyWrapper(Object obj, ReportCallback reportCallback) {
        this.obj = obj;
        this.reportCallback = reportCallback;
    }

    public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable {
        Object result = null;
        try {
            result = m.invoke(obj, args);
            return result;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " +
                    e.getMessage());
        } finally {
            String className = obj.getClass().getName();
            String methodName = m.getName();
            LogRunner.log(reportCallback,className,methodName,args,result,spyConfig);

        }
    }
}
