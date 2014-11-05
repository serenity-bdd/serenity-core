package net.thucydides.junit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that data-driven tests can and should be run in parallel.
 * You can optionally indicate the number of threads to use. If not specified,
 * a sensible value based on the number of processors will be used.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Concurrent {
    String threads() default "";
}
