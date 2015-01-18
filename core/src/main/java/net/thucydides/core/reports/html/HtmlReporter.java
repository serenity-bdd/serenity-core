package net.thucydides.core.reports.html;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.ThucydidesReporter;
import net.thucydides.core.reports.templates.ReportTemplate;
import net.thucydides.core.reports.templates.TemplateManager;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    private final EnvironmentVariables environmentVariables;

    protected static final String TIMESTAMP_FORMAT = "dd-MM-YYYY HH:mm";

    public HtmlReporter() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    public HtmlReporter(final EnvironmentVariables environmentVariables) {
        super();
        this.templateManager = Injectors.getInjector().getInstance(TemplateManager.class);
        this.environmentVariables = environmentVariables;
    }

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

    private boolean alreadyCopied = false;

    protected void copyResourcesToOutputDirectory() throws IOException {
        if (!alreadyCopied) {
            alreadyCopied = true;
            updateResourceDirectoryFromSystemPropertyIfDefined();
            copyResources();
        }
    }

    private void copyResources() throws IOException {
        HtmlResourceCopier copier = new HtmlResourceCopier(getResourceDirectory());
        copier.copyHTMLResourcesTo(getOutputDirectory());
    }

    protected void copyTestResultsToOutputDirectory() throws IOException {
        File testResultsSource = getSourceDirectoryOrDefault();
        if ((!getOutputDirectory().getAbsolutePath().equals(testResultsSource.getAbsolutePath())) && testResultsSource.exists()) {
            FileUtils.copyDirectory(testResultsSource, getOutputDirectory(), withXMLorHTMLorCSVFiles());
        }
    }

    private FileFilter withXMLorHTMLorCSVFiles() {
        return new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".xml")
                        || file.getName().endsWith(".html")
                        || file.getName().endsWith(".csv");
            }
        };
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
        try (BufferedWriter writer = Files.newBufferedWriter(report.toPath(), StandardCharsets.UTF_8)){
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