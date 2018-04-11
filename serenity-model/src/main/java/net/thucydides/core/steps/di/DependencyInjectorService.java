package net.thucydides.core.steps.di;

import net.serenitybdd.core.di.DependencyInjector;

import java.util.List;

public interface DependencyInjectorService {
    List<DependencyInjector> findDependencyInjectors();
}
