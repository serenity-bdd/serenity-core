package net.thucydides.model.requirements;

import net.serenitybdd.model.di.ModelInfrastructure;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.domain.ReportType;
import net.thucydides.model.domain.RequirementCache;
import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.reports.TestOutcomeLoader;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.requirements.reports.RequirementsOutcomeFactory;
import net.thucydides.model.requirements.reports.SerenityJsRequirementsOutcomeFactory;
import net.thucydides.model.util.EnvironmentVariables;

import java.nio.file.Path;
import java.util.List;

import static net.thucydides.model.reports.html.ReportNameProvider.NO_CONTEXT;

public class SerenityJsRequirements implements Requirements {

    private final RequirementsService requirementsService;
    private final RequirementsOutcomeFactory requirementsOutcomeFactory;

    public static SerenityJsRequirements from(Path testScenariosDirectory, Path jsonOutcomesDirectory) {
        EnvironmentVariables environmentVariables = ModelInfrastructure.getEnvironmentVariables();
        IssueTracking issueTracking = ModelInfrastructure.getIssueTracking();
        RequirementCache requirementCache = RequirementCache.getInstance();
        TestOutcomeLoader testOutcomeLoader = new TestOutcomeLoader(environmentVariables);
        Path outputDirectory = ConfiguredEnvironment.getConfiguration().getOutputDirectory().toPath();

        SerenityJsTagProvider serenityJsTagProvider = new SerenityJsTagProvider(
                requirementCache,
                testOutcomeLoader,
                environmentVariables,
                testScenariosDirectory,
                jsonOutcomesDirectory,
                outputDirectory
        );

        RequirementsTagProvider testOutcomeTagProvider = new TestOutcomeRequirementsTagProvider(environmentVariables)
                .fromSourceDirectory(jsonOutcomesDirectory);

        return new SerenityJsRequirements(
                environmentVariables,
                issueTracking,
//                serenityJsTagProvider
                testOutcomeTagProvider
        );
    }

    public SerenityJsRequirements(
            EnvironmentVariables environmentVariables,
            IssueTracking issueTracking,
            RequirementsTagProvider tagProvider
    ) {
        this.requirementsService = new AggregateRequirementsService(environmentVariables, tagProvider);
        this.requirementsOutcomeFactory = new SerenityJsRequirementsOutcomeFactory(
                environmentVariables,
                issueTracking,
                new ReportNameProvider(NO_CONTEXT, ReportType.HTML, requirementsService),
                tagProvider
        );
    }

    @Override
    public RequirementsService getRequirementsService() {
        return requirementsService;
    }

    @Override
    public RequirementsOutcomeFactory getRequirementsOutcomeFactory() {
        return requirementsOutcomeFactory;
    }

    @Override
    public List<String> getTypes() {
        return requirementsService.getRequirementTypes();
    }
}
