package net.thucydides.core.reports.html;

import net.serenitybdd.core.di.ModelInfrastructure;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.logging.ConsoleColors;
import net.thucydides.core.logging.LoggingLevel;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.ThucydidesReporter;
import net.thucydides.core.reports.templates.TemplateManager;
import net.thucydides.core.reports.util.CopyDirectory;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Map;

/**
 * An HTML report generates reports in a given directory and uses resources (images,...) from another.
 *
 * @author johnsmart
 */
public abstract class HtmlReporter extends ThucydidesReporter {

    private static final String DEFAULT_RESOURCE_DIRECTORY = "report-resources";
    private static final String DEFAULT_SOURCE_DIR = "target/site/serenity";
    private String resourceDirectory = DEFAULT_RESOURCE_DIRECTORY;
    private final TemplateManager templateManager;
    protected final EnvironmentVariables environmentVariables;
    protected final ConsoleColors colored;

    protected static final String TIMESTAMP_FORMAT = "dd-MM-yyyy HH:mm:ss";
    protected static final String READABLE_TIMESTAMP_FORMAT = "MMM dd, yyyy HH:mm:ss";

    public HtmlReporter() {
        this(SystemEnvironmentVariables.currentEnvironmentVariables() );
    }

    public HtmlReporter(final EnvironmentVariables environmentVariables) {
        super();
        this.templateManager = ModelInfrastructure.getTemplateManager();
        this.environmentVariables = environmentVariables;
        this.colored = new ConsoleColors(environmentVariables);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlReporter.class);

    /**
     * Resources such as CSS stylesheets or images.
     */
    public void setResourceDirectory(final String resourceDirectory) {
        this.resourceDirectory = resourceDirectory;
    }

    public String getResourceDirectory() {
        return resourceDirectory;
    }

    protected EnvironmentVariables getEnvironmentVariables() {
        return environmentVariables;
    }

    protected void copyResourcesToOutputDirectory() throws IOException {
        updateResourceDirectoryFromSystemPropertyIfDefined();
        HtmlResourceCopier.copyHtmlResourcesFrom(getResourceDirectory()).to(getOutputDirectory());
        copyProjectSpecificResources();
    }

    private void copyProjectSpecificResources() throws IOException {
        CopyProjectSpecificResourcesTask projectSpecificResourceCopier = new CopyProjectSpecificResourcesTask();
        projectSpecificResourceCopier.setResourceDirectory(resourceDirectory);
        projectSpecificResourceCopier.setOutputDirectory(getOutputDirectory());
        projectSpecificResourceCopier.setSourceDirectory(getSourceDirectory());
        projectSpecificResourceCopier.generateReports();
    }

    protected void copyTestResultsToOutputDirectory() throws IOException {
        Path sourcePath = getSourceDirectoryOrDefault().toPath();
        Path destinationPath = getOutputDirectory().toPath();
        if (Files.exists(sourcePath) && !Files.isSameFile(sourcePath, destinationPath)) {
            LOGGER.trace("Copying directory contents from {} to {}", sourcePath,destinationPath);
            copyDirectoryContents(sourcePath, destinationPath);
            LOGGER.trace("Copying directory contents from {} to {} done", sourcePath,destinationPath);
        }
    }

    private void copyDirectoryContents(Path sourcePath, Path destinationPath) throws IOException {
        Files.walkFileTree(sourcePath, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                Integer.MAX_VALUE, new CopyDirectory(sourcePath, destinationPath));

    }

    private File getSourceDirectoryOrDefault() {
        String source = (getSourceDirectory() != null) ? getSourceDirectory().getAbsolutePath() : DEFAULT_SOURCE_DIR;
        return new File(source);
    }

    private void updateResourceDirectoryFromSystemPropertyIfDefined() {

        String systemDefinedResourceDirectory
             = ThucydidesSystemProperty.SERENITY_REPORT_RESOURCES.from(environmentVariables);
        if (systemDefinedResourceDirectory != null) {
            setResourceDirectory(systemDefinedResourceDirectory);
        }
    }


    protected void addTimestamp(TestOutcome testOutcome, Map<String, Object> context) {
        context.put("timestamp", TestOutcomeTimestamp.from(testOutcome));
    }

    protected Merger mergeTemplate(final String templateFile) {
        return new Merger(templateFile);
    }

    protected Boolean verboseReporting() {
        return LoggingLevel.definedIn(environmentVariables).isAtLeast(LoggingLevel.VERBOSE);
    }
}
