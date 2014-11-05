package net.thucydides.core.reflection;

import com.google.common.base.Optional;

import java.lang.reflect.Field;

/**
 * Internal class used to set field values inside an object.
 */
public class FieldValue {

    private final Object targetObject;

    public static FieldValue inObject(Object targetObject) {
        return new FieldValue(targetObject);
    }

    public FieldValue(Object targetObject) {
        this.targetObject = targetObject;
    }

    public Optional<Object> fromFieldNamed(String fieldName) {
        try {
            Field field = targetObject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return Optional.of(field.get(targetObject));
        } catch (Exception e) {
            return Optional.absent();
        }
    }
}
