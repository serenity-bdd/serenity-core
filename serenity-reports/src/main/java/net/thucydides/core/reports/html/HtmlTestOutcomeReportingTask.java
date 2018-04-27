package net.thucydides.core.reports.html;

import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HtmlTestOutcomeReportingTask implements ReportingTask {

    private final TestOutcome testOutcome;
    private final File outputDirectory;
    private final RequirementsService requirementsService;
    private final EnvironmentVariables environmentVariables;
    private final IssueTracking issueTracking;

    protected static final Logger LOGGER = LoggerFactory.getLogger(HtmlTestOutcomeReportingTask.class);

    public HtmlTestOutcomeReportingTask(TestOutcome testOutcome, File outputDirectory, RequirementsService requirementsService, EnvironmentVariables environmentVariables, IssueTracking issueTracking) {
        this.testOutcome = testOutcome;
        this.outputDirectory = outputDirectory;
        this.requirementsService = requirementsService;
        this.environmentVariables = environmentVariables;
        this.issueTracking = issueTracking;
    }


    @Override
    public void generateReports() throws IOException {


        HtmlAcceptanceTestReporter reporter = new HtmlAcceptanceTestReporter(environmentVariables,
                                                                             requirementsService,
                                                                             issueTracking);

        reporter.setOutputDirectory(outputDirectory);
        reporter.generateReportFor(testOutcome);
    }


    public static TestOutcomeReportBuilder testOutcomeReportsFor(TestOutcomes testOutcomes) {
        return new TestOutcomeReportBuilder(testOutcomes);
    }

    public static class TestOutcomeReportBuilder {
        private final TestOutcomes testOutcomes;

        public TestOutcomeReportBuilder(TestOutcomes testOutcomes) {
            this.testOutcomes = testOutcomes;
        }

        public Set<ReportingTask> using(final EnvironmentVariables environmentVariables,
                                        final RequirementsService requirementsService,
                                        final File outputDirectory,
                                        final IssueTracking issueTracking) {

            Set<ReportingTask> reportingTasks = new HashSet<>();

            LOGGER.debug("GENERATE TEST OUTCOME REPORTS FOR " + testOutcomes.getOutcomes());

            for(TestOutcome outcome : testOutcomes.getOutcomes()) {
                reportingTasks.add(new HtmlTestOutcomeReportingTask(outcome, outputDirectory, requirementsService, environmentVariables, issueTracking));
            }
            return reportingTasks;
        }
    }
}
