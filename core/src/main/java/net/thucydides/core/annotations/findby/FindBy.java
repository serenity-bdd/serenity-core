package net.thucydides.core.annotations.findby;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FindBy{
  How how() default How.ID;

  String using() default "";

  String id() default "";

  String name() default "";

  String ngModel() default "";

  String className() default "";

  String css() default "";

  String tagName() default "";

  String linkText() default "";

  String partialLinkText() default "";

  String xpath() default "";
  
  String jquery() default "";
  
  String sclocator() default "";
}
