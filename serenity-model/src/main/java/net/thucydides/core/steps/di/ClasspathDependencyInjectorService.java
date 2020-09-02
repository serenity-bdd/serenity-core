package net.thucydides.core.steps.di;

import net.serenitybdd.core.di.DependencyInjector;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class ClasspathDependencyInjectorService implements DependencyInjectorService {

    public List<DependencyInjector> findDependencyInjectors() {
        List<DependencyInjector> dependencyInjectors = new ArrayList<>();

        ServiceLoader<DependencyInjector> serviceLoader = ServiceLoader.load(DependencyInjector.class);

        for (DependencyInjector dependencyInjector : serviceLoader) {
            dependencyInjectors.add(dependencyInjector);
        }
        return dependencyInjectors;
    }
}