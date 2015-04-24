package net.thucydides.core.reports.junit;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.List;
import java.util.Map;

public class JUnitXMLOutcomeReporter  {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(JUnitXMLOutcomeReporter.class);

    private final File outputDirectory;

    private final JUnitXMLConverter junitXMLConverter;

    public JUnitXMLOutcomeReporter(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        junitXMLConverter = new JUnitXMLConverter();
    }

    public void generateReportsFor(TestOutcomes testOutcomes) throws IOException {

        LOGGER.info("GENERATING JUNIT REPORTS");

        Preconditions.checkNotNull(outputDirectory);

        Map<String, List<TestOutcome>> testOutcomesGroupedByTestCase = groupByTestCase(testOutcomes);

        for(String testCase : testOutcomesGroupedByTestCase.keySet()) {
            List<TestOutcome> testCaseOutcomes = testOutcomesGroupedByTestCase.get(testCase);
            String reportFilename = reportFilenameFor(testCaseOutcomes.get(0));
            File report = new File(getOutputDirectory(), reportFilename);
            LOGGER.info("GENERATING JUNIT REPORT " + reportFilename);
            try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(report))){
                junitXMLConverter.write(testCase, testCaseOutcomes, outputStream);
            } catch (ParserConfigurationException e) {
                throw new IOException(e);
            } catch (TransformerException e) {
                throw new IOException(e);
            }
        }
    }

    private String reportFilenameFor(TestOutcome testOutcome) {
        ReportNamer reportNamer = ReportNamer.forReportType(ReportType.XML);
        return "SERENITY-JUNIT-"  + reportNamer.getNormalizedTestNameFor(testOutcome);
    }

    private String filenameCompatible(String testCase) {
        return testCase.replaceAll(" ","_");
    }

    private Map<String, List<TestOutcome>> groupByTestCase(TestOutcomes testOutcomes) {
        Map<String, List<TestOutcome>> groupedTestOutcomes = Maps.newHashMap();
        for(TestOutcome outcome : testOutcomes.getOutcomes()) {
            String testCaseName = outcome.getTestCaseName() != null ? outcome.getTestCaseName() : outcome.getStoryTitle();
            if (groupedTestOutcomes.containsKey(testCaseName)) {
                groupedTestOutcomes.get(testCaseName).add(outcome);
            } else {
                List<TestOutcome> outcomes = Lists.newArrayList();
                outcomes.add(outcome);
                groupedTestOutcomes.put(testCaseName, outcomes);
            }
        }
        return groupedTestOutcomes;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    private String reportFor(final TestOutcome testOutcome) {
        return "SERENITY-TEST-" + testOutcome.getReportName(ReportType.JUNIT);
    }

}