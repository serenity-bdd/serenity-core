package net.thucydides.core.steps.construction;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import javax.annotation.Nullable;
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
            return ConstructionStrategy.STEP_LIBRARY_WITH_WEBDRIVER;
        }
        if (hasAConstructorWithParameters(stepLibraryClass)) {
            return ConstructionStrategy.CONSTRUCTOR_WITH_PARAMETERS;
        }
        if (hasAPagesField(stepLibraryClass)) {
            return ConstructionStrategy.STEP_LIBRARY_WITH_PAGES;
        }
        return ConstructionStrategy.DEFAULT_CONSTRUCTOR;
    }

    public boolean hasDefaultConstructor(){
        return hasAConstructorWithoutParameters(stepLibraryClass);
    }


    private <T> boolean isWebdriverStepClass(final Class<T> stepLibraryClass) {

        return (isAScenarioStepClass(stepLibraryClass)
                || hasAPagesConstructor(stepLibraryClass));
    }

    private <T> boolean hasAPagesConstructor(final Class<T> stepLibraryClass) {
        ImmutableSet<? extends Constructor<?>> constructors = copyOf(stepLibraryClass.getDeclaredConstructors());
        return Iterables.any(constructors, withASinglePagesParameter());

    }

    private <T> boolean hasAConstructorWithParameters(final Class<T> stepLibraryClass) {
        ImmutableSet<? extends Constructor<?>> constructors = copyOf(stepLibraryClass.getDeclaredConstructors());
        return Iterables.any(constructors, withAnyParameters());

    }

    private <T> boolean hasAConstructorWithoutParameters(final Class<T> stepLibraryClass) {
        ImmutableSet<? extends Constructor<?>> constructors = copyOf(stepLibraryClass.getDeclaredConstructors());
        return Iterables.any(constructors, withoutParameters());

    }

    private <T> boolean hasAPagesField(final Class<T> stepLibraryClass) {
        ImmutableSet<Field> fields = copyOf(Fields.of(stepLibraryClass).allFields());
        return Iterables.any(fields, ofTypePages());

    }

    private Predicate<Constructor<?>> withAnyParameters() {
        return new Predicate<Constructor<?>>() {

            @Override
            public boolean apply(Constructor<?> constructor) {
                return ((constructor.getParameterTypes().length > 0));
            }

            public boolean test(@Nullable Constructor<?> input) {
                return apply(input);
            }
        };
    }

    private Predicate<Constructor<?>> withoutParameters() {
        return new Predicate<Constructor<?>>() {

            @Override
            public boolean apply(Constructor<?> constructor) {
                return ((constructor.getParameterTypes().length == 0));
            }

            public boolean test(@Nullable Constructor<?> input) {
                return apply(input);
            }
        };
    }

    private Predicate<Constructor<?>> withASinglePagesParameter() {
        return new Predicate<Constructor<?>>() {

            @Override
            public boolean apply(Constructor<?> constructor) {
                return ((constructor.getParameterTypes().length == 1)
                        && (constructor.getParameterTypes()[0] == Pages.class));
            }

            public boolean test(@Nullable Constructor<?> input) {
                return apply(input);
            }
        };
    }

    private <T> boolean isAScenarioStepClass(final Class<T> stepLibraryClass) {
        return ScenarioSteps.class.isAssignableFrom(stepLibraryClass);
    }

    private Predicate<Field> ofTypePages() {
        return new Predicate<Field>() {
            @Override
            public boolean apply(Field field) {
                return (field.getType() == Pages.class);
            }

            public boolean test(@Nullable Field input) {
                return apply(input);
            }
        };
    }
}
