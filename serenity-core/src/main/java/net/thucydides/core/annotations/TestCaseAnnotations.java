package net.thucydides.core.annotations;

import net.serenitybdd.core.environment.WebDriverConfiguredEnvironment;
import net.thucydides.core.configuration.WebDriverConfiguration;
import net.thucydides.core.requirements.SerenityTestCaseFinder;
import net.thucydides.core.webdriver.DriverConfiguration;
import net.thucydides.core.webdriver.ThucydidesWebDriverSupport;
import net.thucydides.core.webdriver.WebdriverManager;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static net.thucydides.core.annotations.ManagedWebDriverAnnotatedField.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Utility class used to inject fields into a test case.
 *
 * @author johnsmart
 */
public final class TestCaseAnnotations {

    private final Object testCase;
    private final DriverConfiguration configuration;
    private static final SerenityTestCaseFinder serenityTestCaseFinder = new SerenityTestCaseFinder();

    public TestCaseAnnotations(final Object testCase, WebDriverConfiguration configuration) {
        this.testCase = testCase;
        this.configuration = configuration;
    }

    public TestCaseAnnotations(final Object testCase) {
        this.testCase = testCase;
        this.configuration = WebDriverConfiguredEnvironment.getDriverConfiguration();
    }

    public static TestCaseAnnotations forTestCase(final Object testCase) {
        return new TestCaseAnnotations(testCase);
    }

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver if the annotated field is present.
     */
    public void injectDriver(final WebDriver driver) {
        java.util.Optional<ManagedWebDriverAnnotatedField> webDriverField
                = findOptionalAnnotatedField(testCase.getClass());
        webDriverField.ifPresent(managedWebDriverAnnotatedField -> managedWebDriverAnnotatedField.setValue(testCase, driver));
    }

    public void injectDrivers(final WebdriverManager webdriverManager) {
        injectDrivers(ThucydidesWebDriverSupport.getDriver(), webdriverManager);
    }

    public void injectDrivers(final WebDriver defaultDriver, final WebdriverManager webdriverManager) {
        List<ManagedWebDriverAnnotatedField> webDriverFields = findAnnotatedFields(testCase.getClass());
        int driverCount = 1;

        String suffix = "";
        for (ManagedWebDriverAnnotatedField webDriverField : webDriverFields) {
            String driverRootName = isNotEmpty(webDriverField.getDriver()) ?  webDriverField.getDriver() : configuredDriverType();
            String driverName = driverRootName + suffix;
            String driverOptions = webDriverField.getOptions();

            if (!ThucydidesWebDriverSupport.getDefaultDriverType().isPresent()) {
                ThucydidesWebDriverSupport.useDefaultDriver(driverName);
                ThucydidesWebDriverSupport.useDriverOptions(driverOptions);
            }

            WebDriver driver = (isEmpty(driverName)) ? defaultDriver : requestedDriverFrom(webdriverManager, webDriverField.getName(), driverName, driverOptions);
            webDriverField.setValue(testCase, driver);

            suffix = nextSuffix(driverCount++);
        }
    }

    private WebDriver requestedDriverFrom(WebdriverManager webdriverManager, String fieldName, String driverName, String driverOptions) {

        return RequestedDrivers.withEnvironmentVariables(configuration.getEnvironmentVariables())
                .andWebDriverManager(webdriverManager)
                .requestedDriverFor(fieldName, driverName, driverOptions);
    }

    private String configuredDriverType() {
        if (ThucydidesWebDriverSupport.isInitialised() && (StringUtils.isNotEmpty(ThucydidesWebDriverSupport.getCurrentDriverName()))) {
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
        return (testClass != null) && findOptionalAnnotatedField(testClass).isPresent();
    }

    public static boolean shouldClearCookiesBeforeEachTestIn(Class<?> testClass) {
        ManagedWebDriverAnnotatedField webDriverField = findFirstAnnotatedField(testClass);
        return webDriverField.getClearCookiesPolicy() == ClearCookiesPolicy.BeforeEachTest;
    }

    public static boolean shouldUsePersistantStepLibraries(Class<?> testClass) {
        return (testClass != null) && (testClass.getAnnotation(UsePersistantStepLibraries.class) != null);
    }

    public static boolean isASerenityTestCase(Class<?> testClass) {
        return serenityTestCaseFinder.isSerenityTestCase(testClass);
    }

}
