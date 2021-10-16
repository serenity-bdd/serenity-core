package net.serenitybdd.core;

import net.serenitybdd.core.di.DependencyInjector;
import net.serenitybdd.core.injectors.EnvironmentDependencyInjector;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.PageObjectDependencyInjector;
import net.thucydides.core.steps.di.DependencyInjectorService;

import java.util.Arrays;
import java.util.List;

public class SerenityDependencyInjectors {

    public static List<DependencyInjector> getDependencyInjectors() {
        List<DependencyInjector> dependencyInjectors = getDependencyInjectorService().findDependencyInjectors();
        dependencyInjectors.addAll(getDefaultDependencyInjectors());
        return dependencyInjectors;
    }

    private static DependencyInjectorService getDependencyInjectorService() {
        return Injectors.getInjector().getInstance(DependencyInjectorService.class);
    }

    private static List<DependencyInjector> getDefaultDependencyInjectors() {
        return Arrays.asList(new PageObjectDependencyInjector(), new EnvironmentDependencyInjector());
    }
}
