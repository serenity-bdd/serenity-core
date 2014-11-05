package net.thucydides.core.pages;

import com.google.common.base.Optional;
import net.thucydides.core.annotations.Fields;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.reflection.FieldSetter;
import net.thucydides.core.steps.InvalidManagedPagesFieldException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * The Pages object keeps track of the Page Objects used during the tests.
 *
 * @author johnsmart
 *
 */
public class PagesAnnotatedField {

    private static final String NO_ANNOTATED_FIELD_ERROR
    = "No Pages field annotated with @ManagedPages was found in the test case.";

    private Field field;
    private ManagedPages annotation;

    /**
     * Find the first field in the class annotated with the <b>Managed</b> annotation.
     */
    public static PagesAnnotatedField findFirstAnnotatedField(final Class<?> testClass) {

        Optional<PagesAnnotatedField> optionalAnnotatedField = findOptionalAnnotatedField(testClass);
        if (optionalAnnotatedField.isPresent()) {
            return optionalAnnotatedField.get();
        } else {
            throw new InvalidManagedPagesFieldException(NO_ANNOTATED_FIELD_ERROR);
        }
    }

    /**
     * Find the first field in the class annotated with the <b>Managed</b> annotation.
     */
    public static Optional<PagesAnnotatedField> findOptionalAnnotatedField(final Class<?> testClass) {

        for (Field field : Fields.of(testClass).allFields()) {
            ManagedPages fieldAnnotation = annotationFrom(field);
            if (fieldAnnotation != null) {
                return Optional.of(new PagesAnnotatedField(field, fieldAnnotation));
            }
        }
        return Optional.absent();
    }

    private static ManagedPages annotationFrom(final Field aField) {
        ManagedPages annotationOnField = null;
        if (isFieldAnnotated(aField)) {
            annotationOnField = aField.getAnnotation(ManagedPages.class);
        }
        return annotationOnField;
    }

    private static boolean isFieldAnnotated(final Field field) {
        return (fieldIsAnnotatedCorrectly(field) && fieldIsRightType(field));
    }

    static boolean fieldIsRightType(final Field field) {
        return (Pages.class.isAssignableFrom(field.getType()));
    }

    private static boolean fieldIsAnnotatedCorrectly(final Field field) {

        boolean pagesAnnotationFound = false;
        for (Annotation annotation : field.getAnnotations()) {
            if (annotation instanceof ManagedPages) {
                pagesAnnotationFound = true;
                break;
            }
        }
        return pagesAnnotationFound;
    }

    protected PagesAnnotatedField(final Field field, final ManagedPages annotation) {
        this.field = field;
        this.annotation = annotation;
    }

    public void setValue(final Object testCase, final Pages pages) {
        try {
            set(testCase).to(pages);
        } catch (IllegalAccessException e) {
            throw new InvalidManagedPagesFieldException("Could not access or set managed pages field: " + field, e);
        }
    }

    protected FieldSetter set(Object targetObject) {
        return new FieldSetter(field, targetObject);
    }

    public String getDefaultBaseUrl() {
        return annotation.defaultUrl();
    }
}
