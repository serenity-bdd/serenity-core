package net.thucydides.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Screenshots {
    boolean onlyOnFailures() default false;
    boolean beforeAndAfterEachStep() default true;
    boolean afterEachStep() default false;
    boolean forEachAction() default false;
    boolean disabled() default false;
}
