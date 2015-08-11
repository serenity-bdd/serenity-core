package net.thucydides.core.annotations;


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Optional.fromNullable;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(Fields.class);

    private final Class<?> clazz;

    public enum FieldValue {
        UNDEFINED
    }

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

    public Set<Field> fieldsAnnotatedBy(Class<? extends Annotation> annotationClass) {
        Set<Field> fields = allFields();
        Set<Field> annotatedFields = new HashSet<>();
        for(Field field : fields) {
            if (field.getAnnotation(annotationClass) != null) {
                annotatedFields.add(field);
            }
        }
        return annotatedFields;
    }

    public static FieldValueBuilder of(Object object) {
        return new FieldValueBuilder(object);
    }

    public static class FieldValueBuilder {
        private final Object object;

        public FieldValueBuilder(Object object) {
            this.object = object;
        }

        public Map<String, Object> asMap() {
            Map<String, Object> fieldValues = Maps.newHashMap();
            for(Field field : Fields.of(object.getClass()).allFields()) {
                try {
                    field.setAccessible(true);
                    if (isValid(field)) {
                        fieldValues.put(field.getName(), fieldValueFrom(field).or(FieldValue.UNDEFINED));
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.warn("Failed to inject the field " + field.getName(), e);
                }
            }
            fieldValues.put("self",object);
            fieldValues.put("this",object);
            return ImmutableMap.copyOf(fieldValues);
        }

        private boolean isValid(Field field) {
            return ((field != null) && (!field.getName().contains("CGLIB")));
        }

        private FieldValueProvider fieldValueFrom(Field field) {
            return new FieldValueProvider(field, object);
        }

        private class FieldValueProvider {
            Field field;
            Object object;

            public FieldValueProvider(Field field, Object object) {
                this.field = field;
                this.object = object;
            }

            public Object or(FieldValue undefinedValue) throws IllegalAccessException {
                return ((field == null) || (object == null)|| (field.get(object) == null)) ? undefinedValue : field.get(object);
            }
        }
    }
}

