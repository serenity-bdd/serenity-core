package net.thucydides.core.annotations;

import net.thucydides.core.model.TestResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a test or test step as pending implementation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Manual {
    /**
     * Specify the most recent result of manual testing.
     * This will expire after a certain number of days, configurable with the "manual.result.expiry.limit" property
     * (by default 15).
     */
    TestResult result() default TestResult.PENDING;

    /**
     * An optional reason explaining the manual result
     */
    String reason() default "";
}
