package net.thucydides.core.annotations;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;

import static ch.lambdaj.Lambda.convert;

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

    public static List<ManagedWebDriverAnnotatedField> findAnnotatedFields(final Class<?> testClass) {
        List<Field> managedFields = ImmutableList.copyOf(Iterables.filter(fieldsIn(testClass), withCorrectAnnotations()));
        return convert(managedFields, toManagedAnnotatedFields());
    }

    private static Converter<Field, ManagedWebDriverAnnotatedField> toManagedAnnotatedFields() {
        return new Converter<Field, ManagedWebDriverAnnotatedField>() {

            @Override
            public ManagedWebDriverAnnotatedField convert(Field field) {
                return new ManagedWebDriverAnnotatedField(field);
            }
        };
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
            @Override
            public boolean apply(Field field) {
                return isFieldAnnotated(field);
            }

            public boolean test(@Nullable Field input) {
                return apply(input);
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

    public ClearCookiesPolicy getClearCookiesPolicy() {
        return field.getAnnotation(Managed.class).clearCookies();
    }

    public String getDriver() {
        return field.getAnnotation(Managed.class).driver();
    }
}
