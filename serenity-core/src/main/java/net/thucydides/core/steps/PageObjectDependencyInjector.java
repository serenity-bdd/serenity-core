package net.thucydides.core.steps;

import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.di.DependencyInjector;
import net.serenitybdd.core.injectors.EnvironmentDependencyInjector;
import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.pages.Pages;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PageObjectDependencyInjector implements DependencyInjector {

    private final Pages pages;

    EnvironmentDependencyInjector environmentDependencyInjector;

    private static final List<Field> NO_FIELDS = new ArrayList<>();

    public PageObjectDependencyInjector(Pages pages) {
        this.pages = pages;
        this.environmentDependencyInjector = new EnvironmentDependencyInjector();
    }

    public void injectDependenciesInto(Object target) {
        environmentDependencyInjector.injectDependenciesInto(target);
        List<Field> pageObjectFields = pageObjectFieldsIn(target);

        updatePageObject(target, pages);
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
        try {
            pageObjectField.setAccessible(true);
            if (pageObjectField.get(target) == null) {
                Class<PageObject> pageObjectClass = (Class<PageObject>) pageObjectField.getType();
                PageObject newPageObject = pages.getPage(pageObjectClass);
                injectDependenciesInto(newPageObject);
                pageObjectField.set(target, newPageObject);
            } else {
                updatePageObject(pageObjectField.get(target), pages);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not instanciate page objects in " + target);
        }
    }

    private void updatePageObject(Object pageObject, Pages pages) {
        if (pageObject instanceof PageObject) {
            ((PageObject) pageObject).setPages(pages);
            ((PageObject) pageObject).setDriver(pages.getDriver());
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
