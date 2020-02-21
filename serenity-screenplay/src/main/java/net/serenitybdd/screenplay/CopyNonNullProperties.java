package net.serenitybdd.screenplay;

import java.lang.reflect.Field;
import java.util.Arrays;

class CopyNonNullProperties {
    private Object source;

    public CopyNonNullProperties(Object source) {
        this.source = source;
    }

    public static CopyNonNullProperties from(Object task) {
        return new CopyNonNullProperties(task);
    }

    public void to(Object target) {
        Arrays.stream(source.getClass().getDeclaredFields()).filter(field -> !field.isSynthetic()).forEach(
                field -> copyFieldValue(field, source, target)
        );
        Arrays.stream(source.getClass().getFields()).filter(field -> !field.isSynthetic()).forEach(
                field -> copyFieldValue(field, source, target)
        );
    }

    private void copyFieldValue(Field field, Object source, Object target) {
        try {
            field.setAccessible(true);
            Object sourceValue = field.get(source);
            if (sourceValue == null) { return; }

            Field targetField = null;
            targetField = targetField(target, field.getName());

            targetField.setAccessible(true);
            targetField.set(target, sourceValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Field targetField(Object target, String name) throws NoSuchFieldException {
        Class performableClass = target.getClass().getSuperclass();
        if (Arrays.stream(performableClass.getDeclaredFields()).anyMatch(field -> field.getName().equals(name))) {
            return performableClass.getDeclaredField(name);
        }
        return performableClass.getField(name);
    }
}
