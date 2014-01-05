package no.steria.spytest.spy;

import no.steria.spytest.serializer.ClassSerializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SpyWrapper implements java.lang.reflect.InvocationHandler {
    private Object obj;
    private ReportCallback reportCallback;

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Object obj,Class<T> givenInterface, ReportCallback reportCallback) {
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
            if (reportCallback.doReport()) {
                String className = obj.getClass().getName();
                String methodName = m.getName();
                if (reportCallback.doReport(className,methodName)) {
                    ClassSerializer classSerializer = new ClassSerializer();
                    StringBuilder parameters=new StringBuilder();
                    if (args != null) {
                        boolean first = true;
                        for (Object para : args) {
                            if (!first) {
                                parameters.append(";");
                            }
                            first = false;
                            parameters.append(classSerializer.asString(para));
                        }
                    }
                    String resultStr = classSerializer.asString(result);
                    reportCallback.event(className,methodName,parameters.toString(),resultStr);
                }

            }
        }
    }
}
