package net.serenitybdd.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a particular test case tests a given user story.
 *
 * <p>Can be used with a string value to name the story directly:</p>
 * <pre>
 * {@literal @}Story("Add item to cart")
 * public class WhenAddingItems { }
 * </pre>
 *
 * <p>Or with a class reference for legacy class-hierarchy-based stories:</p>
 * <pre>
 * {@literal @}Story(storyClass = MyApp.AddItem.class)
 * public class WhenAddingItems { }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Story {
    /** String-based story name. */
    String value() default "";

    /** Legacy class-based story reference. */
    Class<?> storyClass() default void.class;
}
