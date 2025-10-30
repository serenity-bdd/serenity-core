package net.serenitybdd.junit.runners;

import net.thucydides.core.annotations.TestCaseAnnotations;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.webdriver.Configuration;
import org.junit.runners.model.TestClass;

public class TestConfiguration {


    private final Class<?> testClass;
    private final Configuration configuration;
    private final TestClassAnnotations theTestIsAnnotated;

    public TestConfiguration(Class<?> testClass, Configuration configuration) {
        this.testClass = testClass;
        this.configuration = configuration;
        this.theTestIsAnnotated = TestClassAnnotations.forTestClass(testClass);
    }

    public boolean shouldClearMetadata() {
        return (!ThucydidesSystemProperty.SERENITY_MAINTAIN_SESSION.booleanFrom(configuration.getEnvironmentVariables()));
    }

    public static TestConfigurationBuilder forClass(Class<?> testClass) {
        return new TestConfigurationBuilder(testClass);
    }

    @Deprecated
    protected boolean isUniqueSession() {
        return (theTestIsAnnotated.toUseAUniqueSession() || configuration.shouldUseAUniqueBrowser());
    }

    public boolean shouldClearTheBrowserSession() {
        return (isAWebTest() && TestCaseAnnotations.shouldClearCookiesBeforeEachTestIn(testClass().getJavaClass()));
    }

    public boolean shouldResetStepLibraries() {
        return !shouldClearMetadata() && !TestCaseAnnotations.shouldUsePersistantStepLibraries(testClass);
    }

    public static class TestConfigurationBuilder {

        private final Class<?> testClass;

        public TestConfigurationBuilder(Class<?> testClass) {
            this.testClass = testClass;
        }

        public TestConfiguration withSystemConfiguration(Configuration configuration) {
            return new TestConfiguration(testClass, configuration);
        }
    }

    private TestClass testClass() {
        return new TestClass(testClass);
    }

    public boolean isAWebTest() {
        return TestCaseAnnotations.isWebTest(testClass().getJavaClass());
    }

}
