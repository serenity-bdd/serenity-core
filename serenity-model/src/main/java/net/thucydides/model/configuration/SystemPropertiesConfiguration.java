package net.thucydides.model.configuration;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.TakeScreenshots;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.webdriver.Configuration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import static net.thucydides.model.ThucydidesSystemProperty.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Centralized configuration of the test runner. You can configure the output
 * directory, the browser to use, and the reports to generate. Most
 * configuration elements can be set using system properties.
 */
public class SystemPropertiesConfiguration implements Configuration {

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
     * HTML reports will be generated in this directory.
     */
    protected File outputDirectory;

    /**
     * JSON reports will be read from here
     */
    protected File sourceDirectory;

    private File historyDirectory;

    protected String defaultBaseUrl;

    protected EnvironmentVariables environmentVariables;

    private final FilePathParser filePathParser;
    private Path projectDirectory;

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
//        String systemDefinedDirectory = ConfiguredEnvironment.getConfiguration().getOutputDirectory().getPath();

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
        Optional<Long> implicitTimeoutInMilliseconds = webdriverCapabilitiesImplicitTimeoutFrom(environmentVariables);
        return implicitTimeoutInMilliseconds.map(integer -> integer / 1000).orElse(2L).intValue();


    }

    private Optional<Long> webdriverCapabilitiesImplicitTimeoutFrom(EnvironmentVariables environmentVariables) {
        return EnvironmentSpecificConfiguration
                .from(environmentVariables)
                .getOptionalProperty("webdriver.capabilities.timeouts.implicit", "webdriver.timeouts.implicitlywait")
                .map(Long::parseLong);
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

    public void setSourceDirectory(final File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    @Override
    public void setProjectDirectory(Path projectDirectory) {
        this.projectDirectory = projectDirectory;
    }

    @Override
    public Path getProjectDirectory() {
        return projectDirectory;
    }


    /**
     * The output directory is where the test runner writes the XML and HTML
     * reports to. By default, it will be in 'target/site/serenity', but you can
     * override this value either programmatically or by providing a value in
     * the <b>thucydides.output.dir</b> system property.
     *
     * - serenity.outputDirectory is used to override the default output directory for the Serenity reports
     * - project.reporting.OutputDirectory is used by Maven to define the output directory for the whole project (e.g. target/site)
     * - project.build.directory is used by Maven to define the build directory for the whole project (e.g. target)
     */
    public File getOutputDirectory() {

        if (outputDirectory == null) {
            outputDirectory = findOutputDirectory();
        }
        return outputDirectory;
    }

    @Nullable
    private File findOutputDirectory() {
        // The project.reporting.OutputDirectory property can be used to override the output directory used by Maven
        String serenityOutputDirectory = environmentVariables.optionalProperty("serenity.outputDirectory").orElse(null);
        String projectReportingOutputDirectory = environmentVariables.optionalProperty("project.reporting.OutputDirectory").orElse(null);
        String projectBuildDirectory = environmentVariables.optionalProperty("project.build.directory").orElse(null);
        String workingDirectory = (projectDirectory == null) ? environmentVariables.getProperty("user.dir") : projectDirectory.toAbsolutePath().toString();

        // Absolute dirs always prime
        if (serenityOutputDirectory != null && new File(serenityOutputDirectory).isAbsolute()) {
            return new File(serenityOutputDirectory);
        }

        //
        // serenity.outputDirectory is used to override the default output directory for the Serenity reports
        //
        //
        if (isNotEmpty(serenityOutputDirectory)) {
            String baseDir = Optional.ofNullable(projectBuildDirectory).map(dir -> dir + "/")
                    .orElse(Optional.ofNullable(projectReportingOutputDirectory).map(dir -> dir + "/").orElse(""));
            return new File(baseDir + serenityOutputDirectory);
        }
        //
        // project.reporting.OutputDirectory is used by Maven to define the output directory for the whole project (e.g. target/site)
        //
        if (isNotEmpty(projectReportingOutputDirectory)) {
            return new File(projectReportingOutputDirectory + "/serenity");
        }
        //
        // project.build.directory is used by Maven to define the build directory for the whole project (e.g. target)
        //
        else if (isNotEmpty(projectBuildDirectory)) {
            return new File(projectBuildDirectory + "/target/site/serenity");
        }
        else if (isNotEmpty(workingDirectory)) {
            return new File(workingDirectory + "/target/site/serenity");
        }
        // Default directory
        return new File("target/site/serenity");

    }

    public File getSourceDirectory() {
        if (sourceDirectory == null) {
            sourceDirectory = new File(SERENITY_OUTPUT_DIRECTORY.optionalFrom(environmentVariables).orElse("target/site/serenity"));
        }
        return sourceDirectory;
    }

    @Override
    public File getHistoryDirectory() {
        if (historyDirectory == null) {
            historyDirectory = loadHistoryDirectoryFromSystemProperties();
        }
        return historyDirectory;
    }

    public double getEstimatedAverageStepCount() {
        return integerPropertyNamed(SERENITY_ESTIMATED_AVERAGE_STEP_COUNT, 1);
    }

    @SuppressWarnings("deprecation")
    public boolean onlySaveFailingScreenshots() {
        return Boolean.parseBoolean(propertyNamed(THUCYDIDES_ONLY_SAVE_FAILING_SCREENSHOTS, "false"));
    }

    @SuppressWarnings("deprecation")
    public boolean takeVerboseScreenshots() {
        return Boolean.parseBoolean(propertyNamed(THUCYDIDES_VERBOSE_SCREENSHOTS, "false"));
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
        return EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalInteger(property.getPropertyName(), property.getLegacyPropertyName());
    }

    private Integer integerPropertyNamed(ThucydidesSystemProperty property, int defaultValue) {
        return integerPropertyNamed(property).orElse(defaultValue);
    }

    private String propertyNamed(ThucydidesSystemProperty propertyName) {
        return propertyNamed(propertyName, null);
    }
}
