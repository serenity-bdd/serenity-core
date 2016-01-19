package net.thucydides.core.webdriver;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TakeScreenshots;
import net.thucydides.core.steps.FilePathParser;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.thucydides.core.ThucydidesSystemProperty.*;
import static net.thucydides.core.webdriver.WebDriverFactory.getDriverFrom;

/**
 * Centralized configuration of the test runner. You can configure the output
 * directory, the browser to use, and the reports to generate. Most
 * configuration elements can be set using system properties.
 *
 */
public class SystemPropertiesConfiguration implements Configuration {

    private final static Logger LOGGER = LoggerFactory.getLogger(SystemPropertiesConfiguration.class);

    /**
     * The default browser is Firefox.
     */
    public static final String DEFAULT_WEBDRIVER_DRIVER = "firefox";

    /**
     * Default timeout when waiting for AJAX elements in pages, in milliseconds.
     */
    public static final int DEFAULT_ELEMENT_TIMEOUT_SECONDS = 5;

    public static final Integer DEFAULT_ESTIMATED_AVERAGE_STEP_COUNT = 5;

    /**
     * Use this property to define the output directory in which reports will be
     * stored.
     */
    public static final String OUTPUT_DIRECTORY_PROPERTY = ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.getPropertyName();

    private static final String MAVEN_BASE_DIR = "project.basedir";


    /**
     * If in system properties will be defined project.build.directory or project.reporting.OutputDirectory then it will
     * be used for output for serenity test reports.
     * Byt default maven NEVER push this properties to system environment, but they are available in maven pm.
     */
    public static final String MAVEN_BUILD_DIRECTORY = "project.build.directory";

    private static final String MAVEN_REPORTS_DIRECTORY = "project.reporting.OutputDirectory";
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

    public static final String MAX_RETRIES = "max.retries";

    public static final String JUNIT_RETRY_TESTS="junit.retry.tests";

    /**
     * By default, reports will go here.
     */
    private static final String DEFAULT_OUTPUT_DIRECTORY = "target/site/serenity";

    /**
     * HTML and XML reports will be generated in this directory.
     */
    private File outputDirectory;

    private String defaultBaseUrl;

    private final EnvironmentVariables environmentVariables;

    private final FilePathParser filePathParser;

    @Inject
    public SystemPropertiesConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        filePathParser = new FilePathParser(environmentVariables);
    }

    public Configuration copy() {
        return withEnvironmentVariables(environmentVariables);
    }

    public Configuration withEnvironmentVariables(EnvironmentVariables environmentVariables) {
        SystemPropertiesConfiguration copy = new SystemPropertiesConfiguration(environmentVariables.copy());
        copy.outputDirectory = null; // Reset to be reloaded from the System properties
        copy.defaultBaseUrl = defaultBaseUrl;
        return copy;
    }

    public EnvironmentVariables getEnvironmentVariables() {
        return environmentVariables;
    }

    public int maxRetries() {
        return getEnvironmentVariables().getPropertyAsInteger(MAX_RETRIES, 0);
    }

    /**
     * Get the currently-configured browser type.
     */
    public SupportedWebDriver getDriverType() {

        String driverType = getDriverFrom(environmentVariables, DEFAULT_WEBDRIVER_DRIVER);
        return lookupSupportedDriverTypeFor(driverType);
    }

    /**
     * Where should the reports go?
     */
    public File loadOutputDirectoryFromSystemProperties() {
        String systemDirectoryProperty = ThucydidesSystemProperty.THUCYDIDES_OUTPUT_DIRECTORY.from(environmentVariables, getMavenBuildDirectory());
        String instantiatedPath = filePathParser.getInstanciatedPath(systemDirectoryProperty);
        String systemDefinedDirectory = (instantiatedPath != null) ? instantiatedPath : DEFAULT_OUTPUT_DIRECTORY;

        File newOutputDirectory = new File(systemDefinedDirectory);
        if (!newOutputDirectory.isAbsolute()) {
            newOutputDirectory = resolveIfMavenIsUsed(newOutputDirectory);
        }
        newOutputDirectory.mkdirs();
        LOGGER.info("OutputDirectory" + " : " + newOutputDirectory.getAbsolutePath());
        return newOutputDirectory;
    }

    /**
     * should be base on module dir and not root project dir (if multimodule project iused with maven plugin)
     *
     * @param path to dir with reports, "outputDir"
     * @return if maven used, path should be resolved instead module dir but not against working dir.
     */
    private File resolveIfMavenIsUsed(File path) {
        String mavenBuildDirectory = getEnvironmentVariables().getProperty(MAVEN_BUILD_DIRECTORY);
        if (StringUtils.isNotEmpty(mavenBuildDirectory)) {
            return Paths.get(mavenBuildDirectory).resolve(path.toPath()).toFile();
        }
        return path;
    }

    private String getMavenBuildDirectory() {
        LOGGER.info(MAVEN_BUILD_DIRECTORY + " : " + getEnvironmentVariables().getProperty(MAVEN_BUILD_DIRECTORY));
        LOGGER.info(MAVEN_REPORTS_DIRECTORY + " : " + getEnvironmentVariables().getProperty(MAVEN_REPORTS_DIRECTORY));
        String mavenBuildDirectory = getEnvironmentVariables().getProperty(MAVEN_BUILD_DIRECTORY);
        String mavenReportsDirectory = getEnvironmentVariables().getProperty(MAVEN_REPORTS_DIRECTORY);
        String defaultMavenRelativeTargetDirectory = null;
        if (StringUtils.isNotEmpty(mavenReportsDirectory)) {
            defaultMavenRelativeTargetDirectory = mavenReportsDirectory.concat(File.separator).concat("serenity");
        } else if (StringUtils.isNotEmpty(mavenBuildDirectory)) {
            defaultMavenRelativeTargetDirectory = mavenBuildDirectory.concat(File.separator).concat(DEFAULT_OUTPUT_DIRECTORY);
        }
        return defaultMavenRelativeTargetDirectory;
    }

    public int getStepDelay() {
        int stepDelay = 0;

        String stepDelayValue = ThucydidesSystemProperty.THUCYDIDES_STEP_DELAY.from(environmentVariables);
        if ((stepDelayValue != null) && (!stepDelayValue.isEmpty())) {
            stepDelay = Integer.valueOf(stepDelayValue);
        }
        return stepDelay;

    }

    public int getElementTimeout() {
        int elementTimeout = DEFAULT_ELEMENT_TIMEOUT_SECONDS;

        String stepDelayValue = ThucydidesSystemProperty.THUCYDIDES_TIMEOUT.from(environmentVariables);
        if ((stepDelayValue != null) && (!stepDelayValue.isEmpty())) {
            elementTimeout = Integer.valueOf(stepDelayValue);
        }
        return elementTimeout;

    }

    public boolean getUseUniqueBrowser() {
        return ThucydidesSystemProperty.THUCYDIDES_USE_UNIQUE_BROWSER.booleanFrom(getEnvironmentVariables());
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * The output directory is where the test runner writes the XML and HTML
     * reports to. By default, it will be in 'target/site/serenity', but you can
     * override this value either programmatically or by providing a value in
     * the <b>thucydides.output.dir</b> system property.
     *
     */
    public File getOutputDirectory() {
        if (outputDirectory == null) {
            outputDirectory = loadOutputDirectoryFromSystemProperties();
        }
        return outputDirectory;
    }

    public double getEstimatedAverageStepCount() {
        return ThucydidesSystemProperty.THUCYDIDES_ESTIMATED_AVERAGE_STEP_COUNT.integerFrom(environmentVariables, DEFAULT_ESTIMATED_AVERAGE_STEP_COUNT);
    }

    @SuppressWarnings("deprecation")
    public boolean onlySaveFailingScreenshots() {
        return getEnvironmentVariables().getPropertyAsBoolean(ThucydidesSystemProperty.THUCYDIDES_ONLY_SAVE_FAILING_SCREENSHOTS.getPropertyName(), false);
    }

    @SuppressWarnings("deprecation")
    public boolean takeVerboseScreenshots() {
        return getEnvironmentVariables().getPropertyAsBoolean(ThucydidesSystemProperty.THUCYDIDES_VERBOSE_SCREENSHOTS.getPropertyName(), false);
    }

    public Optional<TakeScreenshots> getScreenshotLevel() {
        String takeScreenshotsLevel = THUCYDIDES_TAKE_SCREENSHOTS.from(getEnvironmentVariables());
        if (StringUtils.isNotEmpty(takeScreenshotsLevel)) {
            return Optional.of(TakeScreenshots.valueOf(takeScreenshotsLevel.toUpperCase()));
        } else {
            return Optional.absent();
        }
    }

    public boolean storeHtmlSourceCode() {
        return getEnvironmentVariables().getPropertyAsBoolean(THUCYDIDES_STORE_HTML_SOURCE, false);
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
        return ThucydidesSystemProperty.THUCYDIDES_RESTART_BROWSER_FREQUENCY.integerFrom(environmentVariables);
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
        return environmentVariables.getProperty(WEBDRIVER_BASE_URL.getPropertyName(), defaultBaseUrl);
    }
    /**
     * Transform a driver type into the SupportedWebDriver enum. Driver type can
     * be any case.
     *
     * @throws UnsupportedDriverException
     */
    private SupportedWebDriver lookupSupportedDriverTypeFor(final String driverType) {
        SupportedWebDriver driver = null;
        try {
            driver = SupportedWebDriver.getDriverTypeFor(driverType);
        } catch (IllegalArgumentException iae) {
            throwUnsupportedDriverExceptionFor(driverType);
        }
        return driver;
    }

    private void throwUnsupportedDriverExceptionFor(final String driverType) {
        throw new UnsupportedDriverException(driverType
                + " is not a supported browser. Supported driver values are: "
                + SupportedWebDriver.listOfSupportedDrivers());
    }

}
