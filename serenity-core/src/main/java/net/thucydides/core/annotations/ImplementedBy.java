package net.thucydides.core.annotations;

import net.thucydides.core.pages.WebElementFacadeImpl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Annotation is used to specify the implementation Class of the interface
 *  that extends WebElementFacade. 
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplementedBy {

	Class<? extends WebElementFacadeImpl> value();

}
