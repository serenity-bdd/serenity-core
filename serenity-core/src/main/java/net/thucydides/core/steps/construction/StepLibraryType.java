package net.thucydides.core.steps.construction;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.pages.Pages;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static com.google.common.collect.ImmutableSet.copyOf;

public class StepLibraryType {

    private final Class<?> stepLibraryClass;

    public StepLibraryType(Class<?> stepLibraryClass) {
        this.stepLibraryClass = stepLibraryClass;
    }

    public static StepLibraryType ofClass(final Class<?> stepLibraryClass)  {
        return new StepLibraryType(stepLibraryClass);
    }

    public <T> boolean hasAPagesConstructor() {
        ImmutableSet<Constructor<?>> constructors = copyOf(stepLibraryClass.getDeclaredConstructors());
        return Iterables.any(constructors, withASinglePagesParameter());

    }

    public <T> boolean hasAConstructorWithParameters() {
        ImmutableSet<Constructor<?>> constructors = copyOf(stepLibraryClass.getDeclaredConstructors());
        return Iterables.any(constructors, withAnyParameters());

    }

    public <T> boolean hasAPagesField() {
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

    public static Predicate<Field> ofTypePages() {
        return new Predicate<Field>() {
            public boolean apply(Field field) {
                return (field.getType() == Pages.class);
            }
        };
    }

}
