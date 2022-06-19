package net.serenitybdd.core.annotations.environment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify an environment variable for particular test at runtime.
 * Usage: @EnvironmentProperty
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnvironmentProperties {
    EnvironmentProperty[] value();
}
