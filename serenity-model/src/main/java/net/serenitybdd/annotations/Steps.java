package net.serenitybdd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Marks a class that implements test steps as individual methods.
 * Each method that represents a test step should be marked by the Step annotation.
 * Use the "shared" attribute to use the same instance of a given step library class whenever this class is referred to.
 * This is false by default.
 * The "uniqueInstance" is no longer used.
 * The optional actor attribute can be used to instantiate a String field called actor in a step library.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Steps {
    @Deprecated
    boolean uniqueInstance() default false;
    boolean shared() default false;
    String actor() default "";
}
