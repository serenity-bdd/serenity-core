package net.thucydides.core.steps;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.di.DependencyInjector;
import net.serenitybdd.core.injectors.EnvironmentDependencyInjector;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.steps.UIInteractionSteps;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.PageFactory;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.webdriver.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.serenitybdd.core.Serenity.getStepFactory;

public class PageObjectDependencyInjector implements DependencyInjector {

    EnvironmentDependencyInjector environmentDependencyInjector;
    Configuration configuration;

    private static final List<Field> NO_FIELDS = new ArrayList<>();

    /**
     * @deprecated
     */
    public PageObjectDependencyInjector(Pages pages) {
//        this.pages = pages;
        this();
    }

    public PageObjectDependencyInjector() {
        this.environmentDependencyInjector = new EnvironmentDependencyInjector();
        this.configuration = Injectors.getInjector().getInstance(Configuration.class);
    }

    public void injectDependenciesInto(Object target) {
        environmentDependencyInjector.injectDependenciesInto(target);
        List<Field> pageObjectFields = pageObjectFieldsIn(target);

        updatePageObject(target);
        for(Field pageObjectField : nonAbstract(pageObjectFields)) {
            instantiatePageObjectIfNotAssigned(pageObjectField, target);
        }
    }

    private List<Field> nonAbstract(List<Field> pageObjectFields) {
        List<Field> concretePageObjectFields = new ArrayList<>();
        for(Field field : pageObjectFields) {
            if (!Modifier.isAbstract(field.getType().getModifiers())) {
                concretePageObjectFields.add(field);
            }
        }
        return concretePageObjectFields;
    }

    @Override
    public void reset() {}

    private void instantiatePageObjectIfNotAssigned(Field pageObjectField, Object target) {

        PageFactory factory = new PageFactory(Serenity.getWebdriverManager().getWebdriver());

        try {
            pageObjectField.setAccessible(true);

            if (shouldInstrumentField(pageObjectField)) {
                StepAnnotations.injector().instrumentStepsInField(target, pageObjectField, getStepFactory());
            }

            if (pageObjectField.get(target) != null) {
                updatePageObject(pageObjectField.get(target));//, pages);
                return;
            } else {
                Class<PageObject> pageObjectClass = (Class<PageObject>) pageObjectField.getType();
                PageObject newPageObject = factory.createPageOfType(pageObjectClass);
                injectDependenciesInto(newPageObject);
                pageObjectField.set(target, newPageObject);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not instanciate page objects in " + target);
        }
    }

    private boolean shouldInstrumentField(Field pageObjectField) {
        return (pageObjectField.getDeclaringClass().isAssignableFrom(UIInteractionSteps.class))
                || (pageObjectField.getAnnotation(Steps.class) != null);
    }

    private void updatePageObject(Object pageObject) {//, Pages pages) {
        if (pageObject instanceof PageObject) {
            ((PageObject) pageObject).setDriver(Serenity.getWebdriverManager().getWebdriver());
            if (configuration.getBaseUrl() != null) {
                ((PageObject) pageObject).setDefaultBaseUrl(configuration.getBaseUrl());
            }
        }
    }

    private List<Field> pageObjectFieldsIn(Object target) {
        if (target == null) { return NO_FIELDS; }

        Set<Field> allFields = Fields.of(target.getClass()).allFields();

        return allFields.stream()
                .filter(field -> PageObject.class.isAssignableFrom(field.getType()))
                .collect(Collectors.toList());
    }
}
