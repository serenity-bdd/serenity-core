package net.thucydides.core.configuration;

import com.google.inject.Inject;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TakeScreenshots;
import net.thucydides.core.steps.FilePathParser;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;

import java.io.File;
import java.util.Optional;

import static net.thucydides.core.ThucydidesSystemProperty.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Centralized configuration of the test runner. You can configure the output
 * directory, the browser to use, and the reports to generate. Most
 * configuration elements can be set using system properties.
 */
public class SystemPropertiesConfiguration implements Configuration {

    /**
     * Default timeout when waiting for AJAX elements in pages, in seconds.
     */
    public static final int DEFAULT_ELEMENT_TIMEOUT_SECONDS = 5;

    public static final Integer DEFAULT_ESTIMATED_AVERAGE_STEP_COUNT = 5;

    /**
     * If in system properties will be defined project.build.directory or project.reporting.OutputDirectory then it will
     * be used for output for serenity test reports.
     * By default maven NEVER push this properties to system environment, but they are available in maven.
     * This property is used when maven/gradle build contains sub-projects
     */
    public static final String PROJECT_BUILD_DIRECTORY = "project.build.directory";

    /**
     * This property is used when maven/gradle build conta subprojects by serenity  plugins
     */
    /**
     * By default, when accepting untrusted SSL certificates, assume that these certificates will come from an
     * untrusted issuer or will be self signed. Due to limitation within Firefox, it is easy to find out if the
     * certificate has expired or does not match the host it was served for, but hard to find out if the issuer of
     * the certificate is untrusted. By default, it is assumed that the certificates were not be issued from a trusted
     * CA. If you are receive an "untrusted site" prompt on Firefox when using a certificate that was issued by valid
     * issuer, but has expired or is being served served for a different host (e.g. production certificate served in
     * a testing environment) set this to false.
     */
    public static final String REFUSE_UNTRUSTED_CERTIFICATES
            = ThucydidesSystemProperty.REFUSE_UNTRUSTED_CERTIFICATES.getPropertyName();

    /**
     * HTML and XML reports will be generated in this directory.
     */
    protected File outputDirectory;

    private File historyDirectory;

    protected String defaultBaseUrl;

    protected EnvironmentVariables environmentVariables;

    private final FilePathParser filePathParser;

    @Inject
    public SystemPropertiesConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        filePathParser = new FilePathParser(environmentVariables);
    }

    @Override
    public SystemPropertiesConfiguration withEnvironmentVariables(final EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        return this;
    }

    @Override
    public EnvironmentVariables getEnvironmentVariables() {
        return environmentVariables;
    }

    /**
     * Where should the reports go?
     */
    public File loadOutputDirectoryFromSystemProperties() {

        String systemDefinedDirectory = MavenOrGradleBuildPath.specifiedIn(environmentVariables).getBuildDirectory();

        systemDefinedDirectory = filePathParser.getInstanciatedPath(systemDefinedDirectory);

        File newOutputDirectory = new File(systemDefinedDirectory);

        newOutputDirectory.mkdirs();

        return newOutputDirectory;
    }

    public File loadHistoryDirectoryFromSystemProperties() {

        String systemDefinedDirectory = MavenOrGradleBuildPath.specifiedIn(environmentVariables).getHistoryDirectory();

        systemDefinedDirectory = filePathParser.getInstanciatedPath(systemDefinedDirectory);

        File newOutputDirectory = new File(systemDefinedDirectory);

        newOutputDirectory.mkdirs();

        return newOutputDirectory;
    }

    /**
     * If some property that can change output directory@Override was changed this method should be called
     */
    public void reloadOutputDirectory() {
        setOutputDirectory(loadOutputDirectoryFromSystemProperties());
    }

    @Override
    public int getStepDelay() {
        int stepDelay = 0;

        String stepDelayValue = propertyNamed(SERENITY_STEP_DELAY);
        if ((stepDelayValue != null) && (!stepDelayValue.isEmpty())) {
            stepDelay = Integer.parseInt(stepDelayValue);
        }
        return stepDelay;

    }

    @Override
    public int getElementTimeoutInSeconds() {
        Optional<Integer> serenityDefinedTimeoutInSeconds = integerPropertyNamed(SERENITY_TIMEOUT);
        Optional<Integer> implicitTimeoutInMilliseconds = integerPropertyNamed(WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT);

        if (serenityDefinedTimeoutInSeconds.isPresent()) {
            return serenityDefinedTimeoutInSeconds.get();
        } else {
            return implicitTimeoutInMilliseconds.map(integer -> integer / 1000).orElse(DEFAULT_ELEMENT_TIMEOUT_SECONDS);
        }


    }

    private Optional<Integer> integerValueOf(String value) {
        if ((value != null) && (!value.isEmpty())) {
            return Optional.of(Integer.parseInt(value));
        } else {
            return Optional.empty();
        }

    }

    @Override
    @Deprecated
    public boolean getUseUniqueBrowser() {
        return shouldUseAUniqueBrowser();
    }

    @Deprecated
    public boolean shouldUseAUniqueBrowser() {
        return THUCYDIDES_USE_UNIQUE_BROWSER.booleanFrom(getEnvironmentVariables());
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * The output directory is where the test runner writes the XML and HTML
     * reports to. By default, it will be in 'target/site/serenity', but you can
     * override this value either programmatically or by providing a value in
     * the <b>thucydides.output.dir</b> system property.
     */
    public File getOutputDirectory() {
        if (outputDirectory == null) {
            outputDirectory = loadOutputDirectoryFromSystemProperties();
        }
        return outputDirectory;
    }

    @Override
    public File getHistoryDirectory() {
        if (historyDirectory == null) {
            historyDirectory = loadHistoryDirectoryFromSystemProperties();
        }
        return historyDirectory;
    }

    public double getEstimatedAverageStepCount() {
        return integerPropertyNamed(SERENITY_ESTIMATED_AVERAGE_STEP_COUNT, DEFAULT_ESTIMATED_AVERAGE_STEP_COUNT);
    }

    @SuppressWarnings("deprecation")
    public boolean onlySaveFailingScreenshots() {
        return Boolean.parseBoolean(propertyNamed(THUCYDIDES_ONLY_SAVE_FAILING_SCREENSHOTS, "false"));
//        return getEnvironmentVariables().getPropertyAsBoolean(THUCYDIDES_ONLY_SAVE_FAILING_SCREENSHOTS.getPropertyName(), false);
    }

    @SuppressWarnings("deprecation")
    public boolean takeVerboseScreenshots() {
        return Boolean.parseBoolean(propertyNamed(THUCYDIDES_VERBOSE_SCREENSHOTS, "false"));
//        return getEnvironmentVariables().getPropertyAsBoolean(THUCYDIDES_VERBOSE_SCREENSHOTS.getPropertyName(), false);
    }

    public Optional<TakeScreenshots> getScreenshotLevel() {
        String takeScreenshotsLevel = propertyNamed(SERENITY_TAKE_SCREENSHOTS);
        if (isNotEmpty(takeScreenshotsLevel)) {
            return Optional.of(TakeScreenshots.valueOf(takeScreenshotsLevel.toUpperCase()));
        } else {
            return Optional.empty();
        }
    }

    public void setIfUndefined(String property, String value) {
        if (getEnvironmentVariables().getProperty(property) == null) {
            getEnvironmentVariables().setProperty(property, value);
        }
    }

    /**
     * Override the default base URL manually.
     * Normally only needed for testing.
     */
    public void setDefaultBaseUrl(final String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    public int getRestartFrequency() {
        return SERENITY_RESTART_BROWSER_FREQUENCY.integerFrom(environmentVariables);
    }

    @Override
    public int getCurrentTestCount() {
        return 0;
    }

    /**
     * This is the URL where test cases start.
     * The default value can be overriden using the webdriver.baseurl property.
     * It is also the base URL used to build relative paths.
     */
    public String getBaseUrl() {
        return propertyNamed(WEBDRIVER_BASE_URL, defaultBaseUrl);
    }

    private String propertyNamed(ThucydidesSystemProperty property, String defaultValue) {
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(property.getPropertyName(), property.getLegacyPropertyName())
                .orElse(defaultValue);
    }

    private Optional<Integer> integerPropertyNamed(ThucydidesSystemProperty property) {
        Optional<String> value = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(property.getPropertyName(),
                        property.getLegacyPropertyName());
        return value.map(Integer::parseInt);
    }

    private Integer integerPropertyNamed(ThucydidesSystemProperty property, int defaultValue) {
        return Integer.parseInt(EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty(property.getPropertyName(),
                        property.getLegacyPropertyName())
                .orElse(Integer.toString(defaultValue)));
    }

    private String propertyNamed(ThucydidesSystemProperty propertyName) {
        return propertyNamed(propertyName, null);
    }
}
