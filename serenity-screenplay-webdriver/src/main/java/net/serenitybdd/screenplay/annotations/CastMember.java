package net.serenitybdd.screenplay.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * We use this annotation to inject an actor into a test.
 * If the test has a @Managed driver, the actor will have the ability to interact with this driver.
 * If there are more than one @Managed driver fields in the test, we can use the browserField attribute
 * to assign one of them to this actor.
 * If there are no @Managed driver fields, the actor will be assigned a unique driver automatically.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CastMember {
    /**
     * The value is the name of the actor
     */
    String name() default "";

    /**
     * The description of the actor, which will appear alongside the actor name in the Cast section of the reports.
     */
    String description() default "";

    /**
     * The name of the @Managed-annotated WebDriver field
     * This can be used if there are more than one @Managed driver in a test.
     * If you don't specify this value, a browser will be assigned automatically unless you opt-out entirely
     * by setting the withAssignedBrowser attribute to false.
     */
    String browserField() default "";

    /**
     * Set this attribute to true if you DO NOT want a web driver instance assigned automatically to this actor.
     */
    boolean withAssignedBrowser() default true;

    /**
     * Override the driver used for this actor
     */
    String driver() default "";

    /**
     * Specify driver options (used in conjuntion with the driver attribute)
     */
    String options() default "";
}
