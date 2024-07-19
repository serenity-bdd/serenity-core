package net.serenitybdd.cucumber;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set environment configuration options for this test runner.
 * For example:
 * [AT]SerenityOptions("webdriver.driver=chrome")
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SerenityOptions {
    String value();
}
