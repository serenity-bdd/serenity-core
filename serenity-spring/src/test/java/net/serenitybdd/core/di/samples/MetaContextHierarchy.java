package net.serenitybdd.core.di.samples;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;

import java.lang.annotation.*;

@Documented
@Inherited
@ContextHierarchy(@ContextConfiguration(locations = "/spring/config.xml"))
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MetaContextHierarchy {

}
