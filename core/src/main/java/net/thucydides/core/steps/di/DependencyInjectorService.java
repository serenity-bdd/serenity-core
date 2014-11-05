package net.thucydides.core.steps.di;

import net.thucydides.core.steps.DependencyInjector;

import java.util.List;

public interface DependencyInjectorService {
    List<DependencyInjector> findDependencyInjectors();
}
