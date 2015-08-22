package net.thucydides.core.steps.construction;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static com.google.common.collect.ImmutableSet.copyOf;

public class StepLibraryConstructionStrategy {

    private final Class<?> stepLibraryClass;

    private StepLibraryConstructionStrategy(Class<?> stepLibraryClass) {
        this.stepLibraryClass = stepLibraryClass;
    }

    public static StepLibraryConstructionStrategy forClass(Class<?> scenarioStepsClass) {
        return new StepLibraryConstructionStrategy(scenarioStepsClass);
    }

    public ConstructionStrategy getStrategy() {
        if (isWebdriverStepClass(stepLibraryClass)) {
            return ConstructionStrategy.WEBDRIVER_ENABLED_STEP_LIBRARY;
        }
        if (hasAConstructorWithParameters(stepLibraryClass)) {
            return ConstructionStrategy.CONSTRUCTOR_WITH_PARAMETERS;
        }
        return ConstructionStrategy.DEFAULT_CONSTRUCTOR;
    }


    private <T> boolean isWebdriverStepClass(final Class<T> stepLibraryClass) {

        return (isAScenarioStepClass(stepLibraryClass)
                || hasAPagesConstructor(stepLibraryClass)
                || hasAPagesField(stepLibraryClass));
    }

    private <T> boolean hasAPagesConstructor(final Class<T> stepLibraryClass) {
        ImmutableSet<Constructor<?>> constructors = copyOf(stepLibraryClass.getDeclaredConstructors());
        return Iterables.any(constructors, withASinglePagesParameter());

    }

    private <T> boolean hasAConstructorWithParameters(final Class<T> stepLibraryClass) {
        ImmutableSet<Constructor<?>> constructors = copyOf(stepLibraryClass.getDeclaredConstructors());
        return Iterables.any(constructors, withAnyParameters());

    }

    private <T> boolean hasAPagesField(final Class<T> stepLibraryClass) {
        ImmutableSet<Field> fields = copyOf(Fields.of(stepLibraryClass).allFields());
        return Iterables.any(fields, ofTypePages());

    }

    private Predicate<Constructor> withAnyParameters() {
        return new Predicate<Constructor>() {

            public boolean apply(Constructor constructor) {
                return ((constructor.getParameterTypes().length > 0));
            }
        };
    }

    private Predicate<Constructor> withASinglePagesParameter() {
        return new Predicate<Constructor>() {

            public boolean apply(Constructor constructor) {
                return ((constructor.getParameterTypes().length == 1)
                        && (constructor.getParameterTypes()[0] == Pages.class));
            }
        };
    }

    private <T> boolean isAScenarioStepClass(final Class<T> stepLibraryClass) {
        return ScenarioSteps.class.isAssignableFrom(stepLibraryClass);
    }

    private Predicate<Field> ofTypePages() {
        return new Predicate<Field>() {
            public boolean apply(Field field) {
                return (field.getType() == Pages.class);
            }
        };
    }
}
