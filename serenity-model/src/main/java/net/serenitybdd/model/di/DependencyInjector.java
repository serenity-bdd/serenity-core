package net.serenitybdd.model.di;

public interface DependencyInjector {
    /**
     * Injects dependencies into a test class or test step object.
     */
    void injectDependenciesInto(Object object);

    /**
     * Reinitializes dependencies.
     * This ensures that fresh dependency instances will be used for each new test or scenario.
     */
    void reset();
}
