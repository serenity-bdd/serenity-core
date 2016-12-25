package net.thucydides.core.reports.html;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
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

import static net.thucydides.core.reports.html.HtmlResourceCopier.copyHtmlResourcesFrom;

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
    private final EnvironmentVariables environmentVariables;
    private final Charset charset;

    protected static final String TIMESTAMP_FORMAT = "dd-MM-YYYY HH:mm";

    public HtmlReporter() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    public HtmlReporter(final EnvironmentVariables environmentVariables) {
        super();
        this.templateManager = Injectors.getInjector().getInstance(TemplateManager.class);
        this.environmentVariables = environmentVariables;
        this.charset = Charset.forName(ThucydidesSystemProperty.JSON_CHARSET.from(environmentVariables,
                                                                                  StandardCharsets.UTF_8.name()));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlReporter.class);

    private TemplateManager getTemplateManager() {
        return templateManager;
    }

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
        copyHtmlResourcesFrom(getResourceDirectory()).to(getOutputDirectory());
    }

    protected void copyTestResultsToOutputDirectory() throws IOException {
        Path sourcePath = getSourceDirectoryOrDefault().toPath();
        Path destinationPath = getOutputDirectory().toPath();
        if (Files.exists(sourcePath) && !Files.isSameFile(sourcePath, destinationPath)) {
            LOGGER.debug("Copying directory contents from {} to {}", sourcePath,destinationPath);
            copyDirectoryContents(sourcePath, destinationPath);
            LOGGER.debug("Copying directory contents from {} to {} done", sourcePath,destinationPath);
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
             = ThucydidesSystemProperty.THUCYDIDES_REPORT_RESOURCES.from(environmentVariables);
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


}