package net.serenitybdd.core.annotations.environment;

import java.lang.annotation.*;

/**
 * Specify an environment variable for particular test at runtime.
 * Usage: @EnvironmentProperty
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(EnvironmentProperties.class)
public @interface EnvironmentProperty {
    String name();
    String value();
}
