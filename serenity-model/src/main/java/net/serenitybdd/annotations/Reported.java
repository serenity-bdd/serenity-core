package net.serenitybdd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a WebDriver field as one that is managed by the Test Runner.
 * The Thucydides Test Runner will instantiate this WebDriver before the tests start,
 * and close it once they have all finished.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Reported {}
