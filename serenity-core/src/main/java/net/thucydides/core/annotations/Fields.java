package net.thucydides.core.annotations;


import com.google.common.base.Optional;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Find the annotated fields in a given class.
 * Used as a utility class for the higher-level annotation processing.
 * Typical use:
 * <pre>
 *     <code>
 *         for (Field field : Fields.of(someClass).allFields()) {
 *             ...
 *         }
 *     </code>
 * </pre>
 */
public class Fields {

    private final Class<?> clazz;

    public static Fields of(final Class<?> testClass) {
        return new Fields(testClass);
    }

    private Fields(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Set<Field> allFields() {
        Set<Field> fields = new HashSet<Field>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        fields.addAll(Arrays.asList(clazz.getFields()));
        if (clazz != Object.class) {
            fields.addAll(Fields.of(clazz.getSuperclass()).allFields());
        }
        return fields;
    }

    public Set<Field> nonStaticFields() {
        Set<Field> fields = allFields();
        Set<Field> nonStaticFields = new HashSet<Field>();
        for(Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                nonStaticFields.add(field);
            }
        }
        return nonStaticFields;

    }

    public Optional<Field> withName(String pages) {
        for(Field field : allFields()) {
            if (field.getName().equals(pages)){
                return Optional.of(field);
            }
        }
        return Optional.absent();
    }
}

