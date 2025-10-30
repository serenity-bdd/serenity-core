package net.serenitybdd.core.pages;

import net.serenitybdd.annotations.Fields;
import net.serenitybdd.annotations.ManagedPages;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.InvalidManagedPagesFieldException;
import net.thucydides.model.reflection.FieldSetter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * The Pages object keeps track of the Page Objects used during the tests.
 *
 * @author johnsmart
 *
 */
public class PagesAnnotatedField {

    private Field field;
    private ManagedPages annotation;

    /**
     * Find the first field in the class annotated with the <b>Managed</b> annotation.
     */
    public static Optional<PagesAnnotatedField> findFirstAnnotatedField(final Class<?> testClass) {
        return findOptionalAnnotatedField(testClass);
    }

    /**
     * Find the first field in the class annotated with the <b>ManagedPages</b> annotation.
     */
    public static Optional<PagesAnnotatedField> findOptionalAnnotatedField(final Class<?> testClass) {

        for (Field field : Fields.of(testClass).allFields()) {
            ManagedPages fieldAnnotation = annotationFrom(field);
            if (fieldAnnotation != null) {
                return Optional.of(new PagesAnnotatedField(field, fieldAnnotation));
            }
        }
        return Optional.empty();
    }

    private static ManagedPages annotationFrom(final Field aField) {
        ManagedPages annotationOnField = null;
        if (fieldIsAnnotatedCorrectly(aField)) {
            if (!fieldIsRightType(aField)) {
                throw new InvalidManagedPagesFieldException("@ManagedPages field must be of type Pages");
            }
            annotationOnField = aField.getAnnotation(ManagedPages.class);
        }
        return annotationOnField;
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

    public Class<?> getFieldType() {
        return this.field.getType();
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
