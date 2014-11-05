package net.thucydides.core.steps;

import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.di.ClasspathDependencyInjectorService;
import net.thucydides.core.steps.samples.FlatScenarioStepsWithSpringDependencies;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * A description goes here.
 * User: johnsmart
 * Date: 21/06/12
 * Time: 4:44 PM
 */
public class WhenInjectingSpringDependenciesIntoAStepLibrary {

    @Mock
    Pages pages;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_inject_dependencies_into_step_library() {
        SpringDependencyInjector dependencyInjector = new SpringDependencyInjector();

        FlatScenarioStepsWithSpringDependencies steps = new FlatScenarioStepsWithSpringDependencies(pages);
        dependencyInjector.injectDependenciesInto(steps);

        assertThat(steps.widgetService, is(notNullValue()));
        assertThat(steps.catalogService, is(notNullValue()));

    }

    @Test
    public void should_find_spring_dependency_injector_service() {
        ClasspathDependencyInjectorService dependencyInjectorService = new ClasspathDependencyInjectorService();

        List<DependencyInjector> dependencyInjectors = dependencyInjectorService.findDependencyInjectors();

        assertThat(dependencyInjectors.size(), greaterThan(0));
    }

}
