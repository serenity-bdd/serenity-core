package net.thucydides.core.reports.junit;

import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.io.SafelyMoveFiles;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import io.vavr.collection.List;

import static io.vavr.API.List;

public class JUnitXMLOutcomeReporter  {

    private static final Logger LOGGER = LoggerFactory.getLogger(JUnitXMLOutcomeReporter.class);

    private final File outputDirectory;

    private final JUnitXMLConverter junitXMLConverter;

    public final static String FILE_PREFIX = "SERENITY-JUNIT-";

    public JUnitXMLOutcomeReporter(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        junitXMLConverter = new JUnitXMLConverter();
    }

    public void generateReportsFor(TestOutcomes testOutcomes) throws IOException {

        LOGGER.debug("GENERATING JUNIT REPORTS");

        Map<String, List<TestOutcome>> testOutcomesGroupedByTestCase = groupByTestCase(testOutcomes);

        for(String testCase : testOutcomesGroupedByTestCase.keySet()) {
            List<TestOutcome> testCaseOutcomes = testOutcomesGroupedByTestCase.get(testCase);
            String reportFilename = reportFilenameFor(testCaseOutcomes.get(0));
            String unique = UUID.randomUUID().toString();
            File temporary = new File(getOutputDirectory(), reportFilename.concat(unique));
            File report = new File(getOutputDirectory(), reportFilename);
            report.createNewFile();

            LOGGER.debug("GENERATING JUNIT REPORT {} using temporary file {}", reportFilename, temporary);
            try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(temporary))){
                junitXMLConverter.write(testCase, testCaseOutcomes.asJava(), outputStream);
                outputStream.flush();
            } catch (ParserConfigurationException | TransformerException e) {
                throw new IOException(e);
            }
            SafelyMoveFiles.withMaxRetriesOf(3).from(temporary.toPath()).to(report.toPath());
        }
    }

    private String reportFilenameFor(TestOutcome testOutcome) {
        ReportNamer reportNamer = ReportNamer.forReportType(ReportType.XML);
        return FILE_PREFIX  + reportNamer.getNormalizedTestNameFor(testOutcome);
    }

    private Map<String, List<TestOutcome>> groupByTestCase(TestOutcomes testOutcomes) {
        Map<String, List<TestOutcome>> groupedTestOutcomes = new HashMap<>();
        for(TestOutcome outcome : testOutcomes.getOutcomes()) {
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