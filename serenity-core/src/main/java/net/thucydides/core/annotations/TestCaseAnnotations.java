package net.thucydides.core.annotations;

import com.google.common.base.Optional;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebdriverManager;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static net.thucydides.core.annotations.ManagedWebDriverAnnotatedField.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Utility class used to inject fields into a test case.
 * @author johnsmart
 *
 */
public final class TestCaseAnnotations {

    private final Object testCase;
    private final SystemPropertiesConfiguration configuration;

    public TestCaseAnnotations(final Object testCase, SystemPropertiesConfiguration configuration) {
        this.testCase = testCase;
        this.configuration = configuration;
    }

    public TestCaseAnnotations(final Object testCase) {
        this.testCase = testCase;
        this.configuration = Injectors.getInjector().getInstance(SystemPropertiesConfiguration.class);
    }

    public static TestCaseAnnotations forTestCase(final Object testCase) {
        return new TestCaseAnnotations(testCase);
    }

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver if the annotated field is present.
     */
    public void injectDriver(final WebDriver driver) {
        Optional<ManagedWebDriverAnnotatedField> webDriverField
                = findOptionalAnnotatedField(testCase.getClass());
        if (webDriverField.isPresent()) {
            webDriverField.get().setValue(testCase, driver);
        }
    }

    public void injectDrivers(final WebdriverManager webdriverManager) {
        List<ManagedWebDriverAnnotatedField> webDriverFields = findAnnotatedFields(testCase.getClass());
        int driverCount = 1;

        String suffix = "";
        for(ManagedWebDriverAnnotatedField webDriverField : webDriverFields) {
            System.out.println("Injecting webdriver field for " + webDriverField);
            System.out.println("Webdriver field driver attribute =" + webDriverField.getDriver());
            System.out.println("Configured driver type =" + configuredDriverType());
            String driverRootName = isNotEmpty(webDriverField.getDriver()) ?  webDriverField.getDriver() : configuredDriverType();


            String driverName = driverRootName + suffix;
            webDriverField.setValue(testCase, webdriverManager.getWebdriver(driverName));

            suffix = nextSuffix(driverCount++);
        }
    }

    private String configuredDriverType() {
        if (ThucydidesWebDriverSupport.isInitialised()) {
            return ThucydidesWebDriverSupport.getCurrentDriverName();
        }
        return configuration.getDriverType().name();
    }

    private String nextSuffix(int driverCount) {
        return String.format(":%d", driverCount + 1);
    }

    /**
     * Does this class support web tests?
     * Test cases that support web tests need to have at least a WebDriver field annotated with the @Managed
     * annotation.
     */
    public static boolean supportsWebTests(Class clazz) {
        return hasManagedWebdriverField(clazz);
    }

    public boolean isUniqueSession() {
        return isUniqueSession(testCase.getClass());
    }


    public static boolean isUniqueSession(Class<?> testClass) {
        ManagedWebDriverAnnotatedField webDriverField = findFirstAnnotatedField(testClass);
        return webDriverField.isUniqueSession();
    }

    public static boolean isWebTest(Class<?> testClass) {
        return findOptionalAnnotatedField(testClass).isPresent();
    }

    public static boolean shouldClearCookiesBeforeEachTestIn(Class<?> testClass) {
        ManagedWebDriverAnnotatedField webDriverField = findFirstAnnotatedField(testClass);
        return webDriverField.getClearCookiesPolicy() == ClearCookiesPolicy.BeforeEachTest;
    }
}
