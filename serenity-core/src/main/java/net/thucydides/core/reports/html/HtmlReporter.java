package net.thucydides.core.reports.html;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.ThucydidesReporter;
import net.thucydides.core.reports.templates.ReportTemplate;
import net.thucydides.core.reports.templates.TemplateManager;
import net.thucydides.core.reports.util.CopyDirectory;
import net.thucydides.core.util.EnvironmentVariables;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
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
        this.charset = Charset.forName(ThucydidesSystemProperty.JSON_CHARSET.from(environmentVariables, Charset.defaultCharset().name()));
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

    /**
     * Write the actual HTML report to a file with the specified name in the output directory.
     */
    protected File writeReportToOutputDirectory(final String reportFilename, final String htmlContents) throws
            IOException {
        File report = new File(getOutputDirectory(), reportFilename);
        writeToFile(htmlContents, report);
        return report;
    }

    private void writeToFile(String htmlContents, File report) throws IOException {
        String lines[] = htmlContents.split("\\r?\\n");
        try (BufferedWriter writer = Files.newBufferedWriter(report.toPath(), charset)){
            for(String line : lines){
                writer.write(line);
                writer.newLine();
            }
        }
    }

    protected String timestampFrom(TestOutcomes rootOutcomes) {
        return timestampFrom(currentTime());
    }

    protected String timestampFrom(DateTime startTime) {
        return startTime == null ? "" : startTime.toString(TIMESTAMP_FORMAT);
    }

    protected void addTimestamp(TestOutcome testOutcome, Map<String, Object> context) {
        context.put("timestamp", timestampFrom(currentTime()));
    }

    protected DateTime currentTime() {
        return new DateTime();
    }

    protected Merger mergeTemplate(final String templateFile) {
        return new Merger(templateFile);
    }

    protected class Merger {
        final String templateFile;

        public Merger(final String templateFile) {
            this.templateFile = templateFile;
        }

        public String usingContext(final Map<String, Object> context) {
            try {
                ReportTemplate template = getTemplateManager().getTemplateFrom(templateFile);
                StringWriter sw = new StringWriter();
                template.merge(context, sw);
                return sw.toString();
            } catch (Exception e) {
                throw new RuntimeException("Failed to merge template: " + e.getMessage(), e);
            }
        }
    }

}