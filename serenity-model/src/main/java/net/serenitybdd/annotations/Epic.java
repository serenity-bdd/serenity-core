package net.serenitybdd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test class or method as belonging to a named epic.
 * Epics sit above features in the requirements hierarchy: Epic &gt; Feature &gt; Story.
 *
 * <p>Usage:</p>
 * <pre>
 * {@literal @}Epic("E-commerce")
 * public class WhenPurchasingAnItem { }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Epic {
    String value();
}
