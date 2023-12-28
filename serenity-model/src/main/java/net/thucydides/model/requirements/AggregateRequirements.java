package net.thucydides.model.requirements;

import net.thucydides.model.domain.ReportType;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.issues.SystemPropertiesIssueTracking;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.requirements.reports.FileSystemRequirmentsOutcomeFactory;
import net.thucydides.model.requirements.reports.RequirementsOutcomeFactory;
import net.thucydides.model.util.EnvironmentVariables;

import java.nio.file.Path;
import java.util.List;

import static net.thucydides.model.reports.html.ReportNameProvider.NO_CONTEXT;

/**
 * Created by john on 25/06/2016.
 */
public class AggregateRequirements implements Requirements {

    private final RequirementsService requirementsService;
    private final RequirementsOutcomeFactory requirementsOutcomeFactory;

    public AggregateRequirements(Path jsonOutcomes, String featureFilesDirectory) {
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();

        this.requirementsService = new AggregateRequirementsService(
                environmentVariables,
                new FileSystemRequirementsTagProvider(featureFilesDirectory, environmentVariables),
                new TestOutcomeRequirementsTagProvider(environmentVariables).fromSourceDirectory(jsonOutcomes)
        );
        this.requirementsOutcomeFactory = new FileSystemRequirmentsOutcomeFactory(
                environmentVariables,
                new SystemPropertiesIssueTracking(environmentVariables),
                new ReportNameProvider(NO_CONTEXT, ReportType.HTML, this.requirementsService),
                featureFilesDirectory
        );
    }

    public RequirementsOutcomeFactory getRequirementsOutcomeFactory() {
        return requirementsOutcomeFactory;
    }

    public RequirementsService getRequirementsService() {
        return requirementsService;
    }

    public List<String> getTypes() {
        return requirementsService.getRequirementTypes();
    }
}
