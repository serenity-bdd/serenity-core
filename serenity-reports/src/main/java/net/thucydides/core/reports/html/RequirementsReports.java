package net.thucydides.core.reports.html;

import net.thucydides.core.releases.ReleaseManager;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.requirements.reports.RequirementsOutcomeFactory;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static net.thucydides.core.reports.html.RequirementsTypeReportingTask.requirementTypeReports;

public class RequirementsReports {

    private ReportNameProvider reportNameProvider;
    private RequirementsOutcomeFactory requirementsFactory;
    private ReleaseManager releaseManager;
    private final String relativeLink;

    private final RequirementsService requirementsService;
    private final RequirementsConfiguration requirementsConfiguration;
    private final TestOutcomes testOutcomes;
    final FreemarkerContext freemarker;
    final EnvironmentVariables environmentVariables;
    final File outputDirectory;

    public RequirementsReports(FreemarkerContext freemarker,
                               EnvironmentVariables environmentVariables,
                               File outputDirectory,
                               ReportNameProvider reportNameProvider,
                               RequirementsOutcomeFactory requirementsFactory,
                               RequirementsService requirementsService,
                               String relativeLink,
                               TestOutcomes testOutcomes) {
        this.freemarker = freemarker;
        this.environmentVariables = environmentVariables;
        this.outputDirectory = outputDirectory;
        this.reportNameProvider = reportNameProvider;

        this.requirementsFactory = requirementsFactory;
        this.requirementsService = requirementsService;
        this.requirementsConfiguration = new RequirementsConfiguration(environmentVariables);

//        this.htmlRequirementsReporter = new HtmlRequirementsReporter(relativeLink,
//                                                                     Injectors.getInjector().getInstance(IssueTracking.class),
//                                                                     requirementsService);
//        this.htmlRequirementsReporter.setOutputDirectory(outputDirectory);
        this.testOutcomes = testOutcomes;
        this.relativeLink = relativeLink;
    }

    public static Stream<ReportingTask> requirementsReportsFor(FreemarkerContext freemarker,
                                                               EnvironmentVariables environmentVariables,
                                                               File outputDirectory,
                                                               ReportNameProvider reportNameProvider,
                                                               RequirementsOutcomeFactory requirementsFactory,
                                                               RequirementsService requirementsService,
                                                               String relativeLink,
                                                               TestOutcomes testOutcomes,
                                                               RequirementsOutcomes requirementsOutcomes) throws IOException {

        RequirementsReports reporter = new RequirementsReports(freemarker, environmentVariables, outputDirectory, reportNameProvider, requirementsFactory, requirementsService, relativeLink, testOutcomes);

        return Stream.of(
                // REQUIREMENT TYPES
                requirementTypeReports(requirementsService.getRequirementTypes(),
                        requirementsOutcomes,
                        freemarker,
                        environmentVariables,
                        outputDirectory,
                        reportNameProvider),

                // REQUIREMENT OVERVIEW REPORTS
                Stream.of(new RequirementsOverviewReportingTask(freemarker,
                        environmentVariables,
                        outputDirectory,
                        reportNameProvider,
                        requirementsService,
                        requirementsOutcomes.withoutUnrelatedRequirements(),
                        relativeLink,
                        testOutcomes).asParentRequirement()),
                reporter.reportsForChildRequirements(requirementsOutcomes)
        ).flatMap(stream -> stream);

//        List<ReportingTask> reportingTasks = new ArrayList<>();

//        RequirementsReports reporter = new RequirementsReports(freemarker, environmentVariables, outputDirectory, reportNameProvider, requirementsFactory, requirementsService, relativeLink, testOutcomes);
//
//        reportingTasks.addAll(requirementTypeReports(requirementsService.getRequirementTypes(),
//                                                     requirementsOutcomes,
//                                                     freemarker,
//                                                     environmentVariables,
//                                                     outputDirectory,
//                                                     reportNameProvider));

//        reportingTasks.add(new RequirementsOverviewReportingTask(freemarker,
//                environmentVariables,
//                outputDirectory,
//                reportNameProvider,
//                requirementsService,
//                requirementsOutcomes.withoutUnrelatedRequirements(),
//                relativeLink,
//                testOutcomes).asParentRequirement());

//        reportingTasks.addAll(reporter.reportsForChildRequirements(requirementsOutcomes));

        // Release reports have been removed from version 1.2.1
        // generateReleasesReportFor(testOutcomes, requirementsOutcomes);

//        return reportingTasks;
    }

    private Stream<ReportingTask> reportsForChildRequirements(RequirementsOutcomes requirementsOutcomes) throws IOException {

        return requirementsOutcomes.getRequirementOutcomes().stream().flatMap(
                outcome -> {
                    Requirement requirement = outcome.getRequirement();
                    TestOutcomes testOutcomesForThisRequirement = outcome.getTestOutcomes().forRequirement(requirement);
                    RequirementsOutcomes requirementOutcomesForThisRequirement = requirementsFactory.buildRequirementsOutcomesFrom(requirement, testOutcomesForThisRequirement).withoutUnrelatedRequirements();
                    return nestedRequirementsReportsFor(requirement, requirementOutcomesForThisRequirement);
                }
        );
//        List<ReportingTask> reportingTasks = new ArrayList<>();
//        for (RequirementOutcome outcome : requirementsOutcomes.getRequirementOutcomes()) {
//            Requirement requirement = outcome.getRequirement();
//            TestOutcomes testOutcomesForThisRequirement = outcome.getTestOutcomes().forRequirement(requirement);
//            RequirementsOutcomes requirementOutcomesForThisRequirement = requirementsFactory.buildRequirementsOutcomesFrom(requirement, testOutcomesForThisRequirement).withoutUnrelatedRequirements();
//
//            reportingTasks.addAll(nestedRequirementsReportsFor(requirement, requirementOutcomesForThisRequirement));
//        }
//        return reportingTasks;
    }

    private Stream<ReportingTask> nestedRequirementsReportsFor(Requirement parentRequirement, RequirementsOutcomes requirementsOutcomes) {
        return Stream.concat(
                Stream.of(
                        new RequirementsOverviewReportingTask(freemarker,
                                environmentVariables,
                                outputDirectory,
                                reportNameProvider,
                                requirementsService,
                                requirementsOutcomes,
                                relativeLink,
                                requirementsOutcomes.getTestOutcomes(),
                                reportNameProvider.forRequirement(parentRequirement)).asLeafRequirement()
                        ),
                requirementsReportsForChildRequirements(requirementsOutcomes)
        );
//        String reportName = reportNameProvider.forRequirement(parentRequirement);
//        List<ReportingTask> reportingTasks = new ArrayList<>();
//
//
//        reportingTasks.add(
//                new RequirementsOverviewReportingTask(freemarker,
//                        environmentVariables,
//                        outputDirectory,
//                        reportNameProvider,
//                        requirementsService,
//                        requirementsOutcomes,
//                        relativeLink,
//                        requirementsOutcomes.getTestOutcomes(),
//                        reportName).asLeafRequirement()
//        );
//
//        reportingTasks.addAll(requirementsReportsForChildRequirements(requirementsOutcomes));
//
//        return reportingTasks.stream();
    }

    private Stream<ReportingTask> requirementsReportsForChildRequirements(RequirementsOutcomes requirementsOutcomes) {

        return requirementsOutcomes.getRequirementOutcomes().stream().flatMap(
                outcome -> {
                    Requirement requirement = outcome.getRequirement();
                    TestOutcomes testOutcomesForThisRequirement = outcome.getTestOutcomes().forRequirement(requirement);
                    RequirementsOutcomes requirementOutcomesForThisRequirement = requirementsFactory.buildRequirementsOutcomesFrom(requirement, testOutcomesForThisRequirement).withoutUnrelatedRequirements();
                    return nestedRequirementsReportsFor(requirement, requirementOutcomesForThisRequirement);
                }
        );

//        List<ReportingTask> reportingTasks = new ArrayList<>();
//
//        List<RequirementOutcome> requirementOutcomes = requirementsOutcomes.getRequirementOutcomes();
//        for (RequirementOutcome outcome : requirementOutcomes) {
//            Requirement requirement = outcome.getRequirement();
//            TestOutcomes testOutcomesForThisRequirement = outcome.getTestOutcomes().forRequirement(requirement);
//            RequirementsOutcomes requirementOutcomesForThisRequirement = requirementsFactory.buildRequirementsOutcomesFrom(requirement, testOutcomesForThisRequirement);
//            reportingTasks.addAll(nestedRequirementsReportsFor(requirement, requirementOutcomesForThisRequirement));
//        }
//
//        return reportingTasks;
    }

    public List<String> getRequirementTypes() {
        List<String> types = requirementsService.getRequirementTypes();
        if (types.isEmpty()) {
            return requirementsConfiguration.getRequirementTypes();
        } else {
            return types;
        }
    }
}
