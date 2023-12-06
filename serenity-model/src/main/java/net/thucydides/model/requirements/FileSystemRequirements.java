package net.thucydides.model.requirements;

import net.serenitybdd.model.di.ModelInfrastructure;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.domain.ReportType;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.requirements.reports.FileSystemRequirementsOutcomeFactory;
import net.thucydides.model.requirements.reports.RequirementsOutcomeFactory;

import java.util.List;

import static net.thucydides.model.reports.html.ReportNameProvider.NO_CONTEXT;

/**
 * Created by john on 25/06/2016.
 */
public class FileSystemRequirements implements Requirements {

    private final RequirementsService requirementsService;
    private final RequirementsOutcomeFactory requirmentsOutcomeFactory;

    public FileSystemRequirements(String path) {
        this.requirementsService = new FileSystemRequirementsService(path);
        this.requirmentsOutcomeFactory = new FileSystemRequirementsOutcomeFactory(
                ConfiguredEnvironment.getEnvironmentVariables(),
                ModelInfrastructure.getIssueTracking(),
                new ReportNameProvider(NO_CONTEXT, ReportType.HTML, getRequirementsService()),
                path);
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
