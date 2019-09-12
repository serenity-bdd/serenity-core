package net.thucydides.core.reports.junit;

import io.vavr.collection.List;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomes;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static io.vavr.API.List;

public class JUnitXMLOutcomeReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JUnitXMLOutcomeReporter.class);

    private final File outputDirectory;

    private final JUnitXMLConverter junitXMLConverter;

    public final static String FILE_PREFIX = "SERENITY-JUNIT-";

    public JUnitXMLOutcomeReporter(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        junitXMLConverter = new JUnitXMLConverter();
    }

    public void generateReportsFor(TestOutcomes testOutcomes) {

        groupByTestCase(testOutcomes).forEach((testCase, testCaseOutcomes) -> {

            String reportFilename = reportFilenameFor(testCaseOutcomes.get(0));
            File report = new File(getOutputDirectory(), reportFilename);
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(report))) {
                junitXMLConverter.write(testCase, testCaseOutcomes.asJava(), outputStream);
                outputStream.flush();
            } catch (ParserConfigurationException | TransformerException | IOException e) {
                LOGGER.warn("Failed to generate JUnit XML report", e);
            }
        });
    }

    private String reportFilenameFor(TestOutcome testOutcome) {
        ReportNamer reportNamer = ReportNamer.forReportType(ReportType.XML);
        return FILE_PREFIX + reportNamer.getNormalizedTestNameFor(testOutcome);
    }

    private Map<String, List<TestOutcome>> groupByTestCase(TestOutcomes testOutcomes) {
        Map<String, List<TestOutcome>> groupedTestOutcomes = new HashMap<>();
        for (TestOutcome outcome : testOutcomes.getOutcomes()) {
            String testCaseName = StringUtils.isNotEmpty(outcome.getTestCaseName()) ? outcome.getTestCaseName() : outcome.getStoryTitle();
            List<TestOutcome> currentOutcomes = groupedTestOutcomes.getOrDefault(testCaseName, List());
            groupedTestOutcomes.put(testCaseName, currentOutcomes.append(outcome));
        }
        return groupedTestOutcomes;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

}