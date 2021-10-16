package net.serenitybdd.screenplay.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * We use this annotation to inject an actor into a test
 * If the test has a @Managed driver, the actor will have the abiity to interact with this driver
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CastMember {
    /**
     * The value is the name of the actor
     */
    String name() default "";

    /**
     * The name of the @Managed-annotated WebDriver field
     * This can be used if there are more than one @Managed driver in a test.
     */
    String browser() default "";
}
