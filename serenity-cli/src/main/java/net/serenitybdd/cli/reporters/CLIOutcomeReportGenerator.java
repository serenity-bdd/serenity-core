package net.serenitybdd.cli.reporters;

import net.serenitybdd.model.time.Stopwatch;
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.reports.TestOutcomeStream;
import net.thucydides.model.requirements.Requirements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CLIOutcomeReportGenerator implements CLIReportGenerator {

    private final Path sourceDirectory;
    private final Path destinationDirectory;
    private final String project;
    private final String requirementsDirectory;
    private final String issueTrackerUrl;
    private final String jiraUrl;
    private final String jiraProject;
    private final String jiraUsername;
    private final String jiraPassword;

    private static final Logger LOGGER = LoggerFactory.getLogger(CLIOutcomeReportGenerator.class);


    public CLIOutcomeReportGenerator(Path sourceDirectory,
                                     Path destinationDirectory,
                                     String project,
                                     String issueTrackerUrl,
                                     String jiraUrl,
                                     String jiraProject,
                                     String jiraUsername,
                                     String jiraPassword,
                                     String requirementsDirectory) {
        this.sourceDirectory = sourceDirectory;
        this.destinationDirectory = destinationDirectory;
        this.issueTrackerUrl = issueTrackerUrl;
        this.jiraUrl = jiraUrl;
        this.jiraProject = jiraProject;
        this.jiraUsername = jiraUsername;
        this.jiraPassword = jiraPassword;
        this.project = project;
        this.requirementsDirectory = requirementsDirectory;
    }

    @Override
    public void generateReportsFrom(Path sourceDirectory) throws IOException {

        Requirements requirements = RequirementsStrategy.forDirectory(requirementsDirectory);
        final HtmlAcceptanceTestReporter reporter = new HtmlAcceptanceTestReporter(requirements.getRequirementsService());
        reporter.setSourceDirectory(sourceDirectory.toFile());
        reporter.setOutputDirectory(destinationDirectory.toFile());

        ExecutorService executor = Executors.newFixedThreadPool(8);

        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();

        try (TestOutcomeStream stream = TestOutcomeStream.testOutcomesInDirectory(sourceDirectory)) {
            for (final TestOutcome outcome : stream) {
                Runnable worker = () -> {
                    try {
                        reporter.generateReportFor(outcome);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };
                executor.execute(worker);
            }
        }
        LOGGER.info("Shutting down Test outcome reports generation");
        executor.shutdown();
        while (!executor.isTerminated()) {}

        LOGGER.debug("HTML test reports generated in {} ms", stopwatch.stop());
    }
}
