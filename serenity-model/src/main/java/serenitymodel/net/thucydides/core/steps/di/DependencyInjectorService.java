package serenitymodel.net.thucydides.core.steps.di;

import serenitymodel.net.serenitybdd.core.di.DependencyInjector;

import java.util.List;

public interface DependencyInjectorService {
    List<DependencyInjector> findDependencyInjectors();
}
