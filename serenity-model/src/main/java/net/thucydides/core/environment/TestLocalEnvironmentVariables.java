package net.thucydides.core.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Environment variables set for an individual test, that will override the default environment values.
 */
public class TestLocalEnvironmentVariables {

    static final ThreadLocal<Map<String, String>> THREAD_LOCAL_PROPERTIES = ThreadLocal.withInitial(ConcurrentHashMap::new);

    public static void setProperty(String name, String value) {
        THREAD_LOCAL_PROPERTIES.get().put(name,value);
    }

    public static String getProperty(String name) {
        return THREAD_LOCAL_PROPERTIES.get().get(name);
    }

    public static Map<String, String> getProperties() {
        return new HashMap<>(THREAD_LOCAL_PROPERTIES.get());
    }

    public static void clear() {
        THREAD_LOCAL_PROPERTIES.get().clear();
    }
}
