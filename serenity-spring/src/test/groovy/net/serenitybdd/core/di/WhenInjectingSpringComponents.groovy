package net.serenitybdd.core.di

import net.serenitybdd.core.di.samples.*
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

    def "should inject Spring dependencies into a step library class when using meta ContextConfiguration annotations"() {

        given:
            SpringDependencyInjector dependencyInjector = new SpringDependencyInjector();
            FlatScenarioStepsWithSpringMetaAnnotationDependencies steps = new FlatScenarioStepsWithSpringMetaAnnotationDependencies(pages);
        when:
            dependencyInjector.injectDependenciesInto(steps);
        then:
            steps.widgetService != null && steps.catalogService != null
    }

    def "should inject Spring dependencies into a step library class when using ContextHierarchy"() {

        given:
            SpringDependencyInjector dependencyInjector = new SpringDependencyInjector();
            FlatScenarioStepsWithSpringContextHierarchyDependencies steps = new FlatScenarioStepsWithSpringContextHierarchyDependencies(pages);
        when:
            dependencyInjector.injectDependenciesInto(steps);
        then:
            steps.widgetService != null && steps.catalogService != null
    }

    def "should inject Spring dependencies into a step library class when using meta ContextHierarchy annotations"() {

        given:
            SpringDependencyInjector dependencyInjector = new SpringDependencyInjector();
            FlatScenarioStepsWithSpringMetaAnnotationContextHierarchyDependencies steps = new FlatScenarioStepsWithSpringMetaAnnotationContextHierarchyDependencies(pages);
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

    def "should report error if the Spring autowiring fails"() {

        given:
            SpringDependencyInjector dependencyInjector = new SpringDependencyInjector();
            FlatScenarioStepsWithBrokenSpringDependencies steps = new FlatScenarioStepsWithBrokenSpringDependencies(pages);
        when:
            dependencyInjector.injectDependenciesInto(steps);
        then:
            thrown(IllegalStateException)
    }

}
