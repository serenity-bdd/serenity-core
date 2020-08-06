package net.thucydides.core.reports.xml;

import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestLoader;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.OutcomeFormat;
import net.thucydides.core.reports.io.SafelyMoveFiles;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

import static net.thucydides.core.model.ReportType.XML;

/**
 * Generates acceptance test results in XML form.
 * @deprecated XML outputs are no longer supported.
 */
public class XMLTestOutcomeReporter implements AcceptanceTestReporter, AcceptanceTestLoader {

    private File outputDirectory;

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLTestOutcomeReporter.class);

    private transient String qualifier;

    private final EnvironmentVariables environmentVariables = ConfiguredEnvironment.getEnvironmentVariables();

    private final String encoding;

    public XMLTestOutcomeReporter() {
        encoding = ThucydidesSystemProperty.THUCYDIDES_REPORT_ENCODING.from(environmentVariables, StandardCharsets.UTF_8.name());
    }

    @Override
    public void setQualifier(final String qualifier) {
        this.qualifier = qualifier;
    }

    /**
     * We don't need any resources for XML reports.
     */
    @Override
    public void setResourceDirectory(final String resourceDirectoryPath) {
    }

    @Override
    public String getName() {
        return "xml";
    }

    @Override
    public Optional<OutcomeFormat> getFormat() {
        return Optional.of(OutcomeFormat.XML);
    }

    /**
     * Generate an XML report for a given test run.
     */
    @Override
    public File generateReportFor(final TestOutcome testOutcome) throws IOException {
        TestOutcome storedTestOutcome = testOutcome.withQualifier(qualifier);
        Preconditions.checkNotNull(outputDirectory);
        XStream xstream = new XStream();
        xstream.alias("acceptance-test-run", TestOutcome.class);
        xstream.registerConverter(usingXmlConverter());

        String reportFilename = reportFor(storedTestOutcome);

        String unique = UUID.randomUUID().toString();
        File temporary = new File(getOutputDirectory(), reportFilename.concat(unique));
        File report = new File(getOutputDirectory(), reportFilename);
        report.createNewFile();

        LOGGER.debug("Generating XML report for {} to file {} (using temp file {})", testOutcome.getTitle(), report.getAbsolutePath(), temporary.getAbsolutePath());

        try(
           OutputStream outputStream = new FileOutputStream(temporary);
           OutputStreamWriter writer = new OutputStreamWriter(outputStream, encoding)) {
           xstream.toXML(storedTestOutcome, writer);
           writer.flush();
           LOGGER.debug("XML report generated ({} bytes) {}", report.getAbsolutePath(), report.length());
        }

        SafelyMoveFiles.withMaxRetriesOf(3).from(temporary.toPath()).to(report.toPath());

        return report;
    }

    private TestOutcomeConverter usingXmlConverter() {
        return new TestOutcomeConverter();
    }

    private String reportFor(final TestOutcome testOutcome) {
        return testOutcome.withQualifier(qualifier).getReportName(XML);
    }

    @Override
    public Optional<TestOutcome> loadReportFrom(final Path reportFile) {
        return loadReportFrom(reportFile.toFile());
    }

    @Override
    public Optional<TestOutcome> loadReportFrom(final File reportFile) {
        try(
                InputStream input = new FileInputStream(reportFile);
                InputStreamReader reader = new InputStreamReader(input, encoding);
        ) {
            XStream xstream = new XStream();
            xstream.alias("acceptance-test-run", TestOutcome.class);
            xstream.registerConverter(usingXmlConverter());
            return Optional.of((TestOutcome) xstream.fromXML(reader));
        } catch (CannotResolveClassException e) {
            LOGGER.debug("Failed to resolve a serialised class during report generation: " + reportFile, e);
            return Optional.empty();
        } catch (FileNotFoundException e) {
            LOGGER.debug("Could not find expected file during report generation: " + reportFile);
            return Optional.empty();
        } catch (IOException e) {
            LOGGER.warn("Could not load a report for some reason" + e.getMessage());
            return Optional.empty();
        }
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    @Override
    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public List<TestOutcome> loadReportsFrom(final Path outputDirectory) {
        return loadReportsFrom(outputDirectory.toFile());
    }

    @Override
    public List<TestOutcome> loadReportsFrom(File outputDirectory) {
        File[] reportFiles = getAllXMLFilesFrom(outputDirectory);
        List<TestOutcome> testOutcomes = new ArrayList<>();
        if (reportFiles != null) {
            for (File reportFile : reportFiles) {
                testOutcomes.addAll(loadReportFrom(reportFile).map(Collections::singleton).orElse(Collections.emptySet()));
            }
        }
        return testOutcomes;
    }

    private File[] getAllXMLFilesFrom(final File reportsDirectory) {
        return reportsDirectory.listFiles(new XmlFilenameFilter());
    }

    private static final class XmlFilenameFilter implements FilenameFilter {
        public boolean accept(final File file, final String filename) {
            return filename.toLowerCase(Locale.getDefault()).endsWith(".xml");
        }
    }

}
