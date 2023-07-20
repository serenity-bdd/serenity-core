package net.thucydides.core.environment;

import net.thucydides.core.util.EnvironmentVariables;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Environment variables set for an individual test, that will override the default environment values.
 */
public class TestLocalEnvironmentVariables {

    static final ThreadLocal<Map<String, String>> THREAD_LOCAL_PROPERTIES = ThreadLocal.withInitial(ConcurrentHashMap::new);
    private static final ThreadLocal<EnvironmentVariables> CURRENT_ENVIRONMENT_VARIABLES
            = ThreadLocal.withInitial(SystemEnvironmentVariables::createEnvironmentVariables);
    private static final ThreadLocal<EnvironmentVariables> BASE_ENVIRONMENT_VARIABLES
            = ThreadLocal.withInitial(SystemEnvironmentVariables::createEnvironmentVariables);

    public static void setProperty(String name, String value) {
        THREAD_LOCAL_PROPERTIES.get().put(name,value);
        CURRENT_ENVIRONMENT_VARIABLES.set(updatedEnvironmentVariables(CURRENT_ENVIRONMENT_VARIABLES.get()));
    }

    public static String getProperty(String name) {
        return THREAD_LOCAL_PROPERTIES.get().get(name);
    }

    public static Map<String, String> getProperties() {
        return new HashMap<>(THREAD_LOCAL_PROPERTIES.get());
    }

    private static EnvironmentVariables updatedEnvironmentVariables(EnvironmentVariables baseEnvironmentVariables) {
        EnvironmentVariables environmentVariables = baseEnvironmentVariables.copy();
        TestLocalEnvironmentVariables.getProperties().forEach(
                (key, value) -> environmentVariables.setProperty(key, environmentVariables.injectSystemPropertiesInto(value))
        );
        return environmentVariables;
    }

    public static void clear() {
        THREAD_LOCAL_PROPERTIES.get().clear();
        CURRENT_ENVIRONMENT_VARIABLES.set(BASE_ENVIRONMENT_VARIABLES.get());
    }

    public static EnvironmentVariables getUpdatedEnvironmentVariables() {
        return CURRENT_ENVIRONMENT_VARIABLES.get();
    }
}
