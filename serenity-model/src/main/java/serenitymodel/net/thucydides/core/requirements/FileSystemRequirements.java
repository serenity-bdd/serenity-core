package serenitymodel.net.thucydides.core.requirements;

import serenitymodel.net.serenitybdd.core.environment.ConfiguredEnvironment;
import serenitymodel.net.thucydides.core.guice.Injectors;
import serenitymodel.net.thucydides.core.issues.IssueTracking;
import serenitymodel.net.thucydides.core.model.ReportType;
import serenitymodel.net.thucydides.core.reports.html.ReportNameProvider;
import serenitymodel.net.thucydides.core.requirements.reports.FileSystemRequirmentsOutcomeFactory;
import serenitymodel.net.thucydides.core.requirements.reports.RequirementsOutcomeFactory;

import java.util.List;

import static serenitymodel.net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;

/**
 * Created by john on 25/06/2016.
 */
public class FileSystemRequirements implements Requirements {

    private final RequirementsService requirementsService;
    private final RequirementsOutcomeFactory requirmentsOutcomeFactory;

    public FileSystemRequirements(String path) {
        this.requirementsService = new FileSystemRequirementsService(path);
        this.requirmentsOutcomeFactory = new FileSystemRequirmentsOutcomeFactory(
                ConfiguredEnvironment.getEnvironmentVariables(),
                Injectors.getInjector().getInstance(IssueTracking.class),
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
