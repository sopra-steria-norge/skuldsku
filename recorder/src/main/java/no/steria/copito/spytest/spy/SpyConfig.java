package no.steria.copito.spytest.spy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SpyConfig {
    private AsyncMode asyncMode;
    private List<IgnorePara> ignores = new ArrayList<>();

    public boolean isIgnored(String className, String methodName, Object para) {
        if (para == null) {
            return false;
        }
        for (IgnorePara ignorePara : ignores) {
            if (ignorePara.getServiceClass().getName().equals(className) &&
                    (ignorePara.getServiceMethod() == null || ignorePara.getServiceMethod().getName().equals(methodName)) &&
                    para.getClass().isAssignableFrom(ignorePara.getIgnore())
                    ) {
                return true;
            }
        }
        return false;
    }

    public static class Factory {
        private SpyConfig spyConfig = new SpyConfig();

        private Factory() {
        }

        public Factory withAsyncMode(AsyncMode mode) {
            spyConfig.asyncMode = mode;
            return this;
        }

        public SpyConfig create() {
            return spyConfig;
        }

        public Factory ignore(Class<?> serviceClass, Method serviceMethod, Class<?> ignore) {
            spyConfig.ignores.add(new IgnorePara(serviceClass,serviceMethod,ignore));
            return this;
        }
    }

    private SpyConfig() {

    }

    public static Factory factory() {
        return new Factory();
    }

    public AsyncMode getAsyncMode() {
        return asyncMode;
    }
}
