package net.thucydides.core.steps.di;

import com.google.common.collect.Lists;
import net.thucydides.core.steps.DependencyInjector;

import java.util.List;
import java.util.ServiceLoader;

public class ClasspathDependencyInjectorService implements DependencyInjectorService {

    public List<DependencyInjector> findDependencyInjectors() {
        List<DependencyInjector> dependencyInjectors = Lists.newArrayList();

        ServiceLoader<DependencyInjector> serviceLoader = ServiceLoader.load(DependencyInjector.class);

        for (DependencyInjector dependencyInjector : serviceLoader) {
            dependencyInjectors.add(dependencyInjector);
        }
        return dependencyInjectors;
    }
}