package no.steria.spytest.spy;

public class SpyConfig {
    private AsyncMode asyncMode;

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
