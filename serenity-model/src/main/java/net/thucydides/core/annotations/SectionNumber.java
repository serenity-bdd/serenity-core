package net.thucydides.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define the order of appearance of a test method in the Serenity Requirements reports.
 * This has no effect on the order of execution, which is determined by JUnit.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SectionNumber {
    int value() default 0;
}
