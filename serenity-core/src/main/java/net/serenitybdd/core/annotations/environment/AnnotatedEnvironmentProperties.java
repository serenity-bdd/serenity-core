package net.serenitybdd.core.annotations.environment;

import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.environment.TestLocalEnvironmentVariables;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotatedEnvironmentProperties {
    private static List<EnvironmentProperty> environmentPropertiesFor(Method method) {
        return Arrays.asList(method.getAnnotationsByType(EnvironmentProperty.class));
    }

    public static void apply(Method method) {
//        SystemEnvironmentVariables.currentEnvironmentVariables().reset();
        Map<String,String> environmentProperties = new HashMap<>();
        environmentPropertiesFor(method).forEach(
                environmentProperty -> environmentProperties.put(environmentProperty.name(),environmentProperty.value())// TestLocalEnvironmentVariables.setProperty(environmentProperty.name(), environmentProperty.value())
        );
//        SystemEnvironmentVariables.currentEnvironmentVariables().setProperties(TestLocalEnvironmentVariables.getProperties());
        SystemEnvironmentVariables.currentEnvironmentVariables().setProperties(environmentProperties);
    }
}
