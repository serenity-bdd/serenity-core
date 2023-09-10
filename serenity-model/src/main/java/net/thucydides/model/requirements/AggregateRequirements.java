package net.thucydides.model.requirements;

import net.serenitybdd.model.di.ModelInfrastructure;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.domain.ReportType;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.requirements.reports.FileSystemRequirmentsOutcomeFactory;
import net.thucydides.model.requirements.reports.RequirementsOutcomeFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static net.thucydides.model.reports.html.ReportNameProvider.NO_CONTEXT;

/**
 * Created by john on 25/06/2016.
 */
public class AggregateRequirements implements Requirements {

    private final RequirementsService requirementsService;
    private final RequirementsOutcomeFactory requirmentsOutcomeFactory;

    public AggregateRequirements(Path jsonOutcomes, String featureFilesDirectory) {
        this.requirementsService = new AggregateRequirementsService(
                ModelInfrastructure.getEnvironmentVariables(),
                new FileSystemRequirementsTagProvider(featureFilesDirectory),
                new TestOutcomeRequirementsTagProvider().fromSourceDirectory(jsonOutcomes));
        this.requirmentsOutcomeFactory = new FileSystemRequirmentsOutcomeFactory(
                ConfiguredEnvironment.getEnvironmentVariables(),
                ModelInfrastructure.getIssueTracking(),
                new ReportNameProvider(NO_CONTEXT, ReportType.HTML, getRequirementsService()),
                featureFilesDirectory);
    }

    public RequirementsOutcomeFactory getRequirementsOutcomeFactory() {
        return requirmentsOutcomeFactory;
    }

    public RequirementsService getRequirementsService() {
        return requirementsService;
    }

    public List<String> getTypes() {
        return requirementsService.getRequirementTypes();
    }

}
