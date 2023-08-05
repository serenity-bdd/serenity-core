package net.serenitybdd.core.annotations.environment;

import net.thucydides.model.environment.TestLocalEnvironmentVariables;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class AnnotatedEnvironmentProperties {
    private static List<EnvironmentProperty> environmentPropertiesFor(Method method) {
        return Arrays.asList(method.getAnnotationsByType(EnvironmentProperty.class));
    }

    public static void apply(Method method) {
        environmentPropertiesFor(method).forEach(
                environmentProperty ->
                        TestLocalEnvironmentVariables.setProperty(environmentProperty.name(), environmentProperty.value())
        );

    }
}
