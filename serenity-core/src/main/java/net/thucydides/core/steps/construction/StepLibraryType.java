package net.thucydides.core.steps.construction;

import net.serenitybdd.core.collect.NewSet;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.pages.Pages;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.function.Predicate;

public class StepLibraryType {

    private final Class<?> stepLibraryClass;

    public StepLibraryType(Class<?> stepLibraryClass) {
        this.stepLibraryClass = stepLibraryClass;
    }

    public static StepLibraryType ofClass(final Class<?> stepLibraryClass) {
        return new StepLibraryType(stepLibraryClass);
    }

    public boolean hasAPagesConstructor() {
        Set<? extends Constructor<?>> constructors = NewSet.copyOf(stepLibraryClass.getDeclaredConstructors());
        return constructors.stream().anyMatch(withASinglePagesParameter());

    }

    public boolean hasAConstructorWithParameters() {
        Set<? extends Constructor<?>> constructors = NewSet.copyOf(stepLibraryClass.getDeclaredConstructors());
        return constructors.stream().anyMatch(withAnyParameters());

    }

    public boolean hasAPagesField() {
        Set<Field> fields = NewSet.copyOf(Fields.of(stepLibraryClass).allFields());
        return fields.stream().anyMatch(ofTypePages());
    }

    private Predicate<Constructor<?>> withAnyParameters() {
        return constructor -> constructor.getParameterTypes().length > 0;
    }

    private Predicate<Constructor<?>> withASinglePagesParameter() {
        return constructor -> ((constructor.getParameterTypes().length == 1) && (constructor.getParameterTypes()[0] == Pages.class));
    }

    public static Predicate<Field> ofTypePages() {
        return field -> field.getType() == Pages.class;
    }
}
