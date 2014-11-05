package net.thucydides.core.reflection;


import com.google.common.base.Optional;

import java.lang.reflect.Field;

public class FieldFinder {

    private final Class targetClass;

    private FieldFinder(Class targetClass) {
        this.targetClass = targetClass;
    }

    public static FieldFinder inClass(Class targetClass) {
        return new FieldFinder(targetClass);
    }


    public Optional<Field> findFieldCalled(String fieldName) {
        return findFieldCalled(fieldName, targetClass);
    }

    private Optional<Field> findFieldCalled(String fieldName, Class targetClass) {
        Field[] fields = targetClass.getDeclaredFields();
        for(Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return Optional.of(field);
            }
        }
        if (targetClass.getSuperclass() != null) {
            return findFieldCalled(fieldName, targetClass.getSuperclass());
        }
        return Optional.absent();
    }
}
