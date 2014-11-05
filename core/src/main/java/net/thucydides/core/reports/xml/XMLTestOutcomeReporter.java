package net.thucydides.core.reports.xml;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestLoader;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.OutcomeFormat;
import net.thucydides.core.reports.TestOutcomes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import static net.thucydides.core.model.ReportType.XML;

/**
 * Generates acceptance test results in XML form.
 * 
 */
public class XMLTestOutcomeReporter implements AcceptanceTestReporter, AcceptanceTestLoader {

    private File outputDirectory;

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLTestOutcomeReporter.class);

    private transient String qualifier;

    public void setQualifier(final String qualifier) {
        this.qualifier = qualifier;
    }

    /**
     * We don't need any resources for XML reports.
     */
    public void setResourceDirectory(final String resourceDirectoryPath) {
    }

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
    public File generateReportFor(final TestOutcome testOutcome, final TestOutcomes allTestOutcomes) throws IOException {
        TestOutcome storedTestOutcome = testOutcome.withQualifier(qualifier);
        Preconditions.checkNotNull(outputDirectory);
        XStream xstream = new XStream();
        xstream.alias("acceptance-test-run", TestOutcome.class);
        xstream.registerConverter(usingXmlConverter());

        String reportFilename = reportFor(storedTestOutcome);

        OutputStream outputStream = null;
        OutputStreamWriter writer = null;
        File report = new File(getOutputDirectory(), reportFilename);

        LOGGER.info("Generating XML report for {} to file {}", testOutcome.getTitle(), report.getAbsolutePath());

        try {
            outputStream = new FileOutputStream(report);
            writer = new OutputStreamWriter(outputStream, Charset.forName("UTF-8"));
            xstream.toXML(storedTestOutcome, writer);
            LOGGER.info("XML report generated ({} bytes) {}",report.getAbsolutePath(),report.length());
        } catch(IOException failedToWriteReport) {
            throw failedToWriteReport;
        } finally {
            writer.flush();
            writer.close();
            outputStream.close();
        }
        return report;
    }

    private TestOutcomeConverter usingXmlConverter() {
        return new TestOutcomeConverter();
    }

    private String reportFor(final TestOutcome testOutcome) {
        return testOutcome.withQualifier(qualifier).getReportName(XML);
    }

    public Optional<TestOutcome> loadReportFrom(final File reportFile) {
        InputStream input = null;
        InputStreamReader reader = null;
        try {
            XStream xstream = new XStream();
            xstream.alias("acceptance-test-run", TestOutcome.class);
            xstream.registerConverter(usingXmlConverter());
            input = new FileInputStream(reportFile);
            reader = new InputStreamReader(input, Charset.forName("UTF-8"));
            return Optional.of((TestOutcome) xstream.fromXML(reader));
        } catch (CannotResolveClassException e) {
            LOGGER.warn("Tried to load a file that is not a thucydides report: " + reportFile);
            return Optional.absent();
        } catch (FileNotFoundException e) {
            LOGGER.warn("Tried to load a file that is not a thucydides report: " + reportFile);
            return Optional.absent();
        } finally {
            try {
                reader.close();
                input.close();
            } catch (IOException ignored) {}
        }
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public List<TestOutcome> loadReportsFrom(File outputDirectory) {
        File[] reportFiles = getAllXMLFilesFrom(outputDirectory);
        List<TestOutcome> testOutcomes = Lists.newArrayList();
        if (reportFiles != null) {
            for (File reportFile : reportFiles) {
                testOutcomes.addAll(loadReportFrom(reportFile).asSet());
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
