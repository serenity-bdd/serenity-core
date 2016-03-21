package net.serenitybdd.rest.staging.decorators;

import java.lang.reflect.Field;

/**
 * User: YamStranger
 * Date: 3/17/16
 * Time: 12:00 PM
 */
public class ReflectionHelper<T> {
    private final T object;

    public ReflectionHelper(final T object) {
        this.object = object;
    }

    public Object getValueFrom(final String field) throws IllegalAccessException, NoSuchFieldException {
        Field reference = this.object.getClass().getDeclaredField(field);
        if (!reference.isAccessible()) {
            reference.setAccessible(true);
        }
        return reference.get(this.object);
    }

    public void setValueTo(final String field, final Object value) throws NoSuchFieldException, IllegalAccessException {
        Field reference = this.object.getClass().getDeclaredField(field);
        if (!reference.isAccessible()) {
            reference.setAccessible(true);
        }
        reference.set(this.object, value);
    }
}