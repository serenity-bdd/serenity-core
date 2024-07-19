package net.thucydides.model.steps.di;

import net.serenitybdd.model.di.DependencyInjector;

import java.util.List;

public interface DependencyInjectorService {
    List<DependencyInjector> findDependencyInjectors();
}
