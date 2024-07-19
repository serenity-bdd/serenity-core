package net.thucydides.model.reflection;

import java.lang.reflect.Field;

/**
 * Internal class used to set field values inside an object.
 */
public class FieldSetter {

    private final Field field;
    private final Object targetObject;

    public FieldSetter(Field field, Object targetObject) {
        this.field = field;
        this.targetObject = targetObject;
    }

    public void to(Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(targetObject, value);
    }
}
