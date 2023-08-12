package net.thucydides.core.reports.html;

import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.requirements.RequirementsService;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public String reportName() {
        return testOutcome.getReportName();
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

        public Stream<ReportingTask> using(final EnvironmentVariables environmentVariables,
                                           final RequirementsService requirementsService,
                                           final File outputDirectory,
                                           final IssueTracking issueTracking) {
            return testOutcomes.getOutcomes().parallelStream()
                    .map(outcome -> new HtmlTestOutcomeReportingTask(outcome, outputDirectory, requirementsService, environmentVariables, issueTracking));
        }
    }
}
