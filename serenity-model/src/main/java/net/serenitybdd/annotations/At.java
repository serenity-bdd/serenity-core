package net.serenitybdd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define the URLs that a given Page Object works with.
 * A Page Object is designed to work with a particular page. This annotation lets
 * you define a URL or a set of URLs that work with a particular page.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface At {
    String value() default "";
    String[] urls() default {};
}
