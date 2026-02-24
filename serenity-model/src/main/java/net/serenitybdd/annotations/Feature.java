package net.serenitybdd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as representing an application feature.
 *
 * <p>Can be used as a legacy marker annotation (no value) on a class that represents a feature
 * in the class hierarchy, or with a string value to name the feature directly:</p>
 * <pre>
 * {@literal @}Feature("Shopping Cart")
 * public class WhenAddingItems { }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Feature {
    String value() default "";
}
