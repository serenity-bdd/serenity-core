package net.thucydides.core.annotations;

import com.google.common.base.Optional;
import org.openqa.selenium.WebDriver;

/**
 * Utility class used to inject fields into a test case.
 * @author johnsmart
 *
 */
public final class TestCaseAnnotations {

    private final Object testCase;

    private TestCaseAnnotations(final Object testCase) {
        this.testCase = testCase;
    }

    public static TestCaseAnnotations forTestCase(final Object testCase) {
        return new TestCaseAnnotations(testCase);
    }

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver if the annotated field is present.
     */
    public void injectDriver(final WebDriver driver) {
        Optional<ManagedWebDriverAnnotatedField> webDriverField
                = ManagedWebDriverAnnotatedField.findOptionalAnnotatedField(testCase.getClass());
        if (webDriverField.isPresent()) {
            webDriverField.get().setValue(testCase, driver);
        }
    }

    /**
     * Does this class support web tests?
     * Test cases that support web tests need to have at least a WebDriver field annotated with the @Managed
     * annotation.
     */
    public static boolean supportsWebTests(Class clazz) {
        return ManagedWebDriverAnnotatedField.hasManagedWebdriverField(clazz);
    }

    public boolean isUniqueSession() {
        return isUniqueSession(testCase.getClass());
    }


    public static boolean isUniqueSession(Class<?> testClass) {
        ManagedWebDriverAnnotatedField webDriverField = ManagedWebDriverAnnotatedField.findFirstAnnotatedField(testClass);
        return webDriverField.isUniqueSession();
    }

    public static boolean isWebTest(Class<?> testClass) {
        return ManagedWebDriverAnnotatedField.findOptionalAnnotatedField(testClass).isPresent();
    }

}
