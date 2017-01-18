package net.thucydides.core.reports.html;

import net.thucydides.core.releases.ReleaseManager;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.requirements.reports.RequirementOutcome;
import net.thucydides.core.requirements.reports.RequirementsOutcomeFactory;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static Set<ReportingTask> requirementsReportsFor(FreemarkerContext freemarker,
                                                            EnvironmentVariables environmentVariables,
                                                            File outputDirectory,
                                                            ReportNameProvider reportNameProvider,
                                                            RequirementsOutcomeFactory requirementsFactory,
                                                            RequirementsService requirementsService,
                                                            String relativeLink,
                                                            TestOutcomes testOutcomes,
                                                            RequirementsOutcomes requirementsOutcomes) throws IOException {

        Set<ReportingTask> reportingTasks = new HashSet<>();

        RequirementsReports reporter = new RequirementsReports(freemarker, environmentVariables, outputDirectory, reportNameProvider, requirementsFactory, requirementsService, relativeLink, testOutcomes);

        reportingTasks.addAll(requirementTypeReports(requirementsOutcomes, freemarker, environmentVariables, outputDirectory, reportNameProvider));

        reportingTasks.add(new RequirementsOverviewReportingTask(freemarker,
                environmentVariables,
                outputDirectory,
                reportNameProvider,
                requirementsService,
                requirementsOutcomes.withoutUnrelatedRequirements(),
                relativeLink,
                testOutcomes));

        reportingTasks.addAll(reporter.reportsForChildRequirements(requirementsOutcomes));

        // Release reports have been removed from version 1.2.1
        // generateReleasesReportFor(testOutcomes, requirementsOutcomes);

        return reportingTasks;
    }

    private  List<ReportingTask> reportsForChildRequirements(RequirementsOutcomes requirementsOutcomes) throws IOException {

        List<ReportingTask> reportingTasks = new ArrayList<>();
        for (RequirementOutcome outcome : requirementsOutcomes.getRequirementOutcomes()) {
            Requirement requirement = outcome.getRequirement();
            TestOutcomes testOutcomesForThisRequirement = outcome.getTestOutcomes().forRequirement(requirement);
            RequirementsOutcomes requirementOutcomesForThisRequirement = requirementsFactory.buildRequirementsOutcomesFrom(requirement, testOutcomesForThisRequirement).withoutUnrelatedRequirements();

            reportingTasks.addAll(nestedRequirementsReportsFor(requirement, requirementOutcomesForThisRequirement));
        }
        return reportingTasks;
    }

    private List<ReportingTask> nestedRequirementsReportsFor(Requirement parentRequirement, RequirementsOutcomes requirementsOutcomes) throws IOException {
        List<ReportingTask> reportingTasks = new ArrayList<>();

        String reportName = reportNameProvider.forRequirement(parentRequirement);

        reportingTasks.add(
                new RequirementsOverviewReportingTask(freemarker,
                        environmentVariables,
                        outputDirectory,
                        reportNameProvider,
                        requirementsService,
                        requirementsOutcomes,
                        relativeLink,
                        requirementsOutcomes.getTestOutcomes(),
                        reportName)
        );

        reportingTasks.addAll(requirementsReportsForChildRequirements(requirementsOutcomes));

        return reportingTasks;
    }

    private List<ReportingTask> requirementsReportsForChildRequirements(RequirementsOutcomes requirementsOutcomes) throws IOException {
        List<ReportingTask> reportingTasks = new ArrayList<>();

        List<RequirementOutcome> requirementOutcomes = requirementsOutcomes.getRequirementOutcomes();
        for (RequirementOutcome outcome : requirementOutcomes) {
            Requirement requirement = outcome.getRequirement();
            TestOutcomes testOutcomesForThisRequirement = outcome.getTestOutcomes().forRequirement(requirement);
            RequirementsOutcomes requirementOutcomesForThisRequirement = requirementsFactory.buildRequirementsOutcomesFrom(requirement, testOutcomesForThisRequirement);
            reportingTasks.addAll(nestedRequirementsReportsFor(requirement, requirementOutcomesForThisRequirement));
        }

        return reportingTasks;
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
