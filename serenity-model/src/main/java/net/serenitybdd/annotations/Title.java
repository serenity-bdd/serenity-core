package net.serenitybdd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An optional title placed on a step, to override the default step name.
 * This annotation is deprecated, and can be replaced by using the value attribute
 * of the Step annotation.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Title {
    String value();
}
