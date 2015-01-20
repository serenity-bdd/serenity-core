package net.serenitybdd.core.di

import net.serenitybdd.core.di.samples.FlatScenarioStepsWithSpringDependencies
import net.thucydides.core.pages.Pages
import net.thucydides.core.steps.di.ClasspathDependencyInjectorService
import spock.lang.Specification

class WhenInjectingSpringComponents extends Specification {

    Pages pages = Mock()

    def "should inject Spring dependencies into a step library class"() {

        given:
            SpringDependencyInjector dependencyInjector = new SpringDependencyInjector();
            FlatScenarioStepsWithSpringDependencies steps = new FlatScenarioStepsWithSpringDependencies(pages);
        when:
            dependencyInjector.injectDependenciesInto(steps);
        then:
            steps.widgetService != null && steps.catalogService != null
    }

    def "should find the Spring dependency injector service"() {
        given:
            def dependencyInjectorService = new ClasspathDependencyInjectorService();
        when:
            def dependencyInjectors = dependencyInjectorService.findDependencyInjectors();
        then:
            dependencyInjectors.find { it.class == SpringDependencyInjector }
    }
}
