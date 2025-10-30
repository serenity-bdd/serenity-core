package net.serenitybdd.core.di.samples;


import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.*;

@ContextConfiguration(locations = "/spring/config.xml")
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MetaContextConfiguration {

}
