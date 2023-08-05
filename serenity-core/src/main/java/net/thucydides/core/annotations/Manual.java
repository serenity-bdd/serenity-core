package net.thucydides.core.annotations;

import net.thucydides.model.domain.TestResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a manual test.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Manual {
    /**
     * Specify the most recent result of manual testing.
     */
    TestResult result() default TestResult.PENDING;

    /**
     * An optional reason explaining the manual result
     */
    String reason() default "";

    /**
     * An optional indication of the last date or version an appl
     */
    String lastTested() default "";

}
