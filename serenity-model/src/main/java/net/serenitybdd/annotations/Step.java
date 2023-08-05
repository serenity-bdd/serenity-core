package net.serenitybdd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * A step in an acceptance test.
 * Steps are like mini unit-tests, executed in a predetermined order. 
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Step {
    String value() default "";
    boolean fluent() default false;
    boolean callNestedMethods() default true;
    boolean exampleRow() default false;
}
