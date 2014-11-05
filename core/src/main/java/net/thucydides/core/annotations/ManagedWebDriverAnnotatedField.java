package net.thucydides.core.annotations;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;

/**
 * The WebDriver driver is stored as an annotated field in the test classes.
 * 
 * @author johnsmart
 * 
 */
public class ManagedWebDriverAnnotatedField {

    private static final String NO_ANNOTATED_FIELD_ERROR 
                                    = "No WebDriver field annotated with @Managed was found in the test case.";

    private final Field field;

    /**
     * Find the first field in the class annotated with the <b>Managed</b> annotation.
     */
    public static Optional<ManagedWebDriverAnnotatedField> findOptionalAnnotatedField(final Class<?> testClass) {

        try {
            Field annotatedField = Iterables.find(fieldsIn(testClass), withCorrectAnnotations());
            return Optional.of(new ManagedWebDriverAnnotatedField(annotatedField));
        } catch(NoSuchElementException e) {
            return Optional.absent();
        }
    }
    /**
     * Find the first field in the class annotated with the <b>Managed</b> annotation.
     */
    public static ManagedWebDriverAnnotatedField findFirstAnnotatedField(final Class<?> testClass) {

        Optional<ManagedWebDriverAnnotatedField> optionalField = findOptionalAnnotatedField(testClass);
        if (optionalField.isPresent()) {
            return optionalField.get();
        } else {
            throw new InvalidManagedWebDriverFieldException(NO_ANNOTATED_FIELD_ERROR);
        }
    }

    public static boolean hasManagedWebdriverField(final Class<?> testClass) {

        try {
            Iterables.find(fieldsIn(testClass), withCorrectAnnotations());
            return true;
        } catch(NoSuchElementException e) {
            return false;
        }
    }

    private static Predicate<Field> withCorrectAnnotations() {
        return new Predicate<Field>() {
            public boolean apply(Field field) {
                return isFieldAnnotated(field);
            }
        };
    }

    private static boolean isFieldAnnotated(final Field field) {
        return (fieldIsAnnotatedCorrectly(field) && fieldIsRightType(field));
    }

    private static boolean fieldIsRightType(final Field field) {
        return (WebDriverFacade.class.isAssignableFrom(field.getType()) || 
        		field.getType().isAssignableFrom(WebDriver.class));
    }

    private static boolean fieldIsAnnotatedCorrectly(final Field field) {
        return (field.getAnnotation(Managed.class) != null);
    }

    protected ManagedWebDriverAnnotatedField(final Field field) {
        this.field = field;
    }

    public void setValue(final Object testCase, final WebDriver manageDriver) {
        try {
            field.setAccessible(true);
            field.set(testCase, manageDriver);
        } catch (IllegalAccessException e) {
            throw new InvalidManagedWebDriverFieldException("Could not access or set web driver field: " 
                         + field 
                         + " - is this field public?", e);
        }
    }

    private static ImmutableSet<Field> fieldsIn(Class clazz) {
        return ImmutableSet.copyOf(Fields.of(clazz).allFields());
    }

    public boolean isUniqueSession() {
        return field.getAnnotation(Managed.class).uniqueSession();
    }

    public String getDriver() {
        return field.getAnnotation(Managed.class).driver();
    }
}
