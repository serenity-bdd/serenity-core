package net.thucydides.model.steps.di;

import net.serenitybdd.model.di.DependencyInjector;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * This class is responsible for loading and returning instances of
 * {@link DependencyInjector} discovered in the classpath.
 *
 * Thread safety is achieved as the {@link #findDependencyInjectors()} method
 * operates only on local variables, therefore eliminating risks of
 * shared mutable state. However, the thread safety of the returned
 * {@link DependencyInjector} instances will depend on their individual
 * implementations.
 *
 * It uses {@link ServiceLoader} under the hood to load the implementations
 * of {@link DependencyInjector}.
 *
 * <p> Usage: </p>
 * <pre>
 *   ClasspathDependencyInjectorService service = new ClasspathDependencyInjectorService();
 *   List<DependencyInjector> injectors = service.findDependencyInjectors();
 * </pre>
 */
public class ClasspathDependencyInjectorService implements DependencyInjectorService {

    /**
     * Finds and returns all instances of {@link DependencyInjector} available
     * in the classpath at the time of invocation.
     *
     * The {@link ServiceLoader#load(Class)} method is used to discover and
     * instantiate the {@link DependencyInjector} implementations.
     *
     * @return a list of {@link DependencyInjector} instances. If no implementations
     *         are found, an empty list is returned.
     */
    public List<DependencyInjector> findDependencyInjectors() {
        List<DependencyInjector> dependencyInjectors = new ArrayList<>();

        ServiceLoader<DependencyInjector> serviceLoader = ServiceLoader.load(DependencyInjector.class);

        for (DependencyInjector dependencyInjector : serviceLoader) {
            dependencyInjectors.add(dependencyInjector);
        }
        return dependencyInjectors;
    }
}
