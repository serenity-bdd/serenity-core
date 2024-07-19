package net.thucydides.model.environment;

import net.thucydides.model.util.EnvironmentVariables;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Environment variables set for an individual test, that will override the default environment values.
 */
public class TestLocalEnvironmentVariables {

    static final ThreadLocal<Map<String, String>> THREAD_LOCAL_PROPERTIES = ThreadLocal.withInitial(ConcurrentHashMap::new);
    private static final ThreadLocal<EnvironmentVariables> CURRENT_ENVIRONMENT_VARIABLES
            = ThreadLocal.withInitial(() -> SystemEnvironmentVariables.currentEnvironmentVariables().copy());

    public static boolean isEmpty() {
        return THREAD_LOCAL_PROPERTIES.get().isEmpty();
    }

    public static void setProperty(String name, String value) {
        CURRENT_ENVIRONMENT_VARIABLES.get().setProperty(name, value);
        THREAD_LOCAL_PROPERTIES.get().put(name, value);
    }

    public static String getProperty(String name) {
        return THREAD_LOCAL_PROPERTIES.get().get(name);
    }

    public static Map<String, String> getProperties() {
        return new HashMap<>(THREAD_LOCAL_PROPERTIES.get());
    }

    public static void clear() {
        THREAD_LOCAL_PROPERTIES.get().clear();
        CURRENT_ENVIRONMENT_VARIABLES.set(SystemEnvironmentVariables.currentEnvironmentVariables().copy());
    }

    public static EnvironmentVariables getUpdatedEnvironmentVariables() {
        return CURRENT_ENVIRONMENT_VARIABLES.get();
    }
}
