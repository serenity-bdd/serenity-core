package net.thucydides.core.steps.construction;

import net.serenitybdd.annotations.Fields;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Stream;

public class StepLibraryConstructionStrategy {

    private final Class<?> stepLibraryClass;
    private final Constructor<?>[] declaredConstructors;

    private StepLibraryConstructionStrategy(Class<?> stepLibraryClass) {
        this.stepLibraryClass = stepLibraryClass;
        this.declaredConstructors = stepLibraryClass.getDeclaredConstructors();
    }

    public static StepLibraryConstructionStrategy forClass(Class<?> scenarioStepsClass) {
        return new StepLibraryConstructionStrategy(scenarioStepsClass);
    }

    public ConstructionStrategy getStrategy() {
        if (isWebdriverStepClass()) {
            return ConstructionStrategy.STEP_LIBRARY_WITH_WEBDRIVER;
        }
        if (hasAConstructorWithParameters()) {
            return ConstructionStrategy.CONSTRUCTOR_WITH_PARAMETERS;
        }
        if (hasAPagesField()) {
            return ConstructionStrategy.STEP_LIBRARY_WITH_PAGES;
        }
        if (hasAnInnerClassConstructor()) {
            return ConstructionStrategy.INNER_CLASS_CONSTRUCTOR;
        }
        return ConstructionStrategy.DEFAULT_CONSTRUCTOR;
    }

    public boolean hasDefaultConstructor(){
        return hasAConstructorWithoutParameters();
    }


    private <T> boolean isWebdriverStepClass() {

        return (isAScenarioStepClass()  || hasAPagesConstructor());
    }

    private <T> boolean hasAPagesConstructor() {
        return Stream.of(declaredConstructors).anyMatch(
                constructor -> (constructor.getParameterTypes().length == 1) && (constructor.getParameterTypes()[0] == Pages.class)
        );
    }

    private <T> boolean hasAConstructorWithParameters() {
        return Stream.of(declaredConstructors).anyMatch(
                constructor -> (constructor.getParameterTypes().length > 0 && !isInnerClassConstructor(constructor))
        );
    }


    private <T> boolean hasAnInnerClassConstructor() {
        return Stream.of(declaredConstructors).anyMatch(
                constructor -> (isInnerClassConstructor(constructor))
        );
    }

    private boolean isInnerClassConstructor(Constructor<?> constructor) {
        return constructor.getParameters().length == 1
                && constructor.getParameters()[0].getType() == stepLibraryClass.getEnclosingClass();
    }

    private <T> boolean hasAConstructorWithoutParameters() {
        return Stream.of(declaredConstructors).anyMatch(
                constructor -> (constructor.getParameterTypes().length == 0)
        );
    }

    private <T> boolean hasAPagesField() {

        Set<Field> fields = Fields.of(stepLibraryClass).allFields();

        return fields.stream().anyMatch(
                field -> field.getType() == Pages.class
        );

    }

    private <T> boolean isAScenarioStepClass() {
        return ScenarioSteps.class.isAssignableFrom(stepLibraryClass);
    }
}
