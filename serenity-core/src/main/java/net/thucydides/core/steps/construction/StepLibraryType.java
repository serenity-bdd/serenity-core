package net.thucydides.core.steps.construction;

import com.google.common.base.Predicate;
import net.serenitybdd.core.collect.NewSet;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.pages.Pages;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Set;

public class StepLibraryType {

    private final Class<?> stepLibraryClass;

    public StepLibraryType(Class<?> stepLibraryClass) {
        this.stepLibraryClass = stepLibraryClass;
    }

    public static StepLibraryType ofClass(final Class<?> stepLibraryClass) {
        return new StepLibraryType(stepLibraryClass);
    }

    public <T> boolean hasAPagesConstructor() {
        Set<? extends Constructor<?>> constructors = NewSet.copyOf(stepLibraryClass.getDeclaredConstructors());
        return constructors.stream().anyMatch(withASinglePagesParameter());
//                Iterables.any(constructors, withASinglePagesParameter());

    }

    public <T> boolean hasAConstructorWithParameters() {
        Set<? extends Constructor<?>> constructors = NewSet.copyOf(stepLibraryClass.getDeclaredConstructors());
        return constructors.stream().anyMatch(withAnyParameters());
//        return Iterables.any(constructors, withAnyParameters());

    }

    public <T> boolean hasAPagesField() {
        Set<Field> fields = NewSet.copyOf(Fields.of(stepLibraryClass).allFields());
        return fields.stream().anyMatch(ofTypePages());
//        return Iterables.any(fields, ofTypePages());

    }

    private Predicate<Constructor<?>> withAnyParameters() {
        return new Predicate<Constructor<?>>() {

            @Override
            public boolean apply(Constructor<?> constructor) {
                return ((constructor.getParameterTypes().length > 0));
            }


            public boolean test(Constructor<?> input) {
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

            public boolean test(Constructor<?> input) {
                return apply(input);
            }
        };
    }

    public static Predicate<Field> ofTypePages() {
        return new Predicate<Field>() {
            @Override
            public boolean apply(Field input) {
                return (input.getType() == Pages.class);
            }

            public boolean test(Field input) {
                return apply(input);
            }
        };
    }

}
