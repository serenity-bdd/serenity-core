package net.serenitybdd.screenplay;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.reflect.Modifier.*;

class CopyNonNullProperties {
    private Object source;

    public CopyNonNullProperties(Object source) {
        this.source = source;
    }

    public static CopyNonNullProperties from(Object task) {
        return new CopyNonNullProperties(task);
    }

    public void to(Object target) {
        getFields(source.getClass())
                .stream()
                .filter(field -> !field.isSynthetic())
                .filter(field -> !isStatic(field.getModifiers()))
                .forEach(
                        field -> copyFieldValue(field, source, target)
                );
    }

    public static List<Field> getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> classToInspect = clazz;
        while (classToInspect != null) {
            fields.addAll(Arrays.asList(classToInspect.getDeclaredFields()));
            classToInspect = classToInspect.getSuperclass();
        }
        return fields;
    }

    private void copyFieldValue(Field field, Object source, Object target) {
        try {
            field.setAccessible(true);
            Object sourceValue = field.get(source);
            if (sourceValue != null) {
                field.set(target, sourceValue);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
