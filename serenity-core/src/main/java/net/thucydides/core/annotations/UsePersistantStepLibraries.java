package net.thucydides.core.annotations;

import org.junit.runner.Runner;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface UsePersistantStepLibraries {}
