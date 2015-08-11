package net.thucydides.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class specifies the list of ScenarioStep classes provided by a given test library.
 * It is experimental, and used for integration with other applications.
 * @author johnsmart
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StepProvider {

}
