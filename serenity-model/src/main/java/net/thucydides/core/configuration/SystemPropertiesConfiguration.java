package net.thucydides.core.configuration;

import com.google.inject.*;
import net.thucydides.core.*;
import net.thucydides.core.model.*;
import net.thucydides.core.steps.*;
import net.thucydides.core.util.*;
import net.thucydides.core.webdriver.*;

import java.io.*;
import java.util.*;

import static net.thucydides.core.ThucydidesSystemProperty.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Centralized configuration of the test runner. You can configure the output
 * directory, the browser to use, and the reports to generate. Most
 * configuration elements can be set using system properties.
 *
 */
public class SystemPropertiesConfiguration implements Configuration {

    /**
     * Default timeout when waiting for AJAX elements in pages, in milliseconds.
     */
    public static final int DEFAULT_ELEMENT_TIMEOUT_SECONDS = 5;

    public static final Integer DEFAULT_ESTIMATED_AVERAGE_STEP_COUNT = 5;

    /**
     * If in system properties will be defined project.build.directory or project.reporting.OutputDirectory then it will
     * be used for output for serenity test reports.
     * By default maven NEVER push this properties to system environment, but they are available in maven pm.
     * This property is used when maven/gradle build conta subprojects by serenity  plugins
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
    private File outputDirectory;

    private File historyDirectory;

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
    public void reloadOutputDirectory(){
        setOutputDirectory(loadOutputDirectoryFromSystemProperties());
    }

    @Override
    public int getStepDelay() {
        int stepDelay = 0;

        String stepDelayValue = THUCYDIDES_STEP_DELAY.from(environmentVariables);
        if ((stepDelayValue != null) && (!stepDelayValue.isEmpty())) {
            stepDelay = Integer.parseInt(stepDelayValue);
        }
        return stepDelay;

    }

    @Override
    public int getElementTimeout() {
        int elementTimeout = DEFAULT_ELEMENT_TIMEOUT_SECONDS;

        String stepDelayValue = THUCYDIDES_TIMEOUT.from(environmentVariables);
        if ((stepDelayValue != null) && (!stepDelayValue.isEmpty())) {
            elementTimeout = Integer.parseInt(stepDelayValue);
        }
        return elementTimeout;

    }

    @Override
    public boolean getUseUniqueBrowser() {
        return shouldUseAUniqueBrowser();
    }

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
     *
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
        return THUCYDIDES_ESTIMATED_AVERAGE_STEP_COUNT.integerFrom(environmentVariables, DEFAULT_ESTIMATED_AVERAGE_STEP_COUNT);
    }

    @SuppressWarnings("deprecation")
    public boolean onlySaveFailingScreenshots() {
        return getEnvironmentVariables().getPropertyAsBoolean(THUCYDIDES_ONLY_SAVE_FAILING_SCREENSHOTS.getPropertyName(), false);
    }

    @SuppressWarnings("deprecation")
    public boolean takeVerboseScreenshots() {
        return getEnvironmentVariables().getPropertyAsBoolean(THUCYDIDES_VERBOSE_SCREENSHOTS.getPropertyName(), false);
    }

    public Optional<TakeScreenshots> getScreenshotLevel() {
        String takeScreenshotsLevel = THUCYDIDES_TAKE_SCREENSHOTS.from(getEnvironmentVariables());
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
        return THUCYDIDES_RESTART_BROWSER_FREQUENCY.integerFrom(environmentVariables);
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


}
