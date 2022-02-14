package net.serenitybdd.cli;

import com.google.common.collect.ImmutableList;
import net.serenitybdd.cli.reporters.CLIAggregateReportGenerator;
import net.serenitybdd.cli.reporters.CLIIssueTrackerUpdater;
import net.serenitybdd.cli.reporters.CLIReportGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SerenityCLIReportCoordinator {

    private final Path sourceDirectory;
    private final Path destinationDirectory;

    List<CLIReportGenerator> reportGenerators = new ArrayList<>();

    public SerenityCLIReportCoordinator(Path sourceDirectory,
                                        Path destinationDirectory,
                                        String project,
                                        String issueTrackerUrl,
                                        String jiraUrl,
                                        String jiraProject,
                                        String jiraUsername,
                                        String jiraPassword,
                                        String jiraWorkflowActive,
                                        String jiraWorkflow,
                                        String requirementsDirectory,
                                        String tags) {
        this.sourceDirectory = sourceDirectory;
        this.destinationDirectory = destinationDirectory;

        reportGenerators.addAll(ImmutableList.of(
//                new CLIOutcomeReportGenerator(sourceDirectory, destinationDirectory, project, issueTrackerUrl,
//                        jiraUrl, jiraProject, jiraUsername, jiraPassword,
//                        requirementsDirectory),
                new CLIAggregateReportGenerator(sourceDirectory, destinationDirectory, project, issueTrackerUrl,
                        jiraUrl, jiraProject, jiraUsername, jiraPassword,
                        requirementsDirectory, tags),
                new CLIIssueTrackerUpdater(jiraWorkflow, jiraWorkflowActive)
        ));
    }


    public void execute() {
        try {

            printStartingBanner();

            prepareDirectories();

            for (CLIReportGenerator generator : reportGenerators) {
                generator.generateReportsFrom(sourceDirectory);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printStartingBanner() {
        System.out.println("-------------------------------");
        System.out.println("SERENITY COMMAND LINE INTERFACE");
        System.out.println("-------------------------------");
        System.out.println("Loading test outcomes from " + sourceDirectory);
        System.out.println("Writing aggregated report to " + destinationDirectory);
        System.out.println();
    }

    private void prepareDirectories() throws IOException {
        if (Files.notExists(destinationDirectory)) {
            Files.createDirectories(destinationDirectory);
        }
    }
}
