package net.serenitybdd.rest.decorators;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    public Object executeFunction(final String function, final Class[] types, final Object... params)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = this.object.getClass().getDeclaredMethod(function, types);
        method.setAccessible(true);
        return method.invoke(this.object, params.length == 0 ? null : params);
    }
}
