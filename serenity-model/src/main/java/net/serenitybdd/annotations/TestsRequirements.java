package net.serenitybdd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that a test scenario or test step addresses a particular requirement or requirements.
 * The TestsRequirement annotation works with a single annotation, whereas the TestsRequirements
 * annotation accepts several requirements:
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestsRequirements {
    String[] value();
}
