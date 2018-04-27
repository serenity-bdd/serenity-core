package net.thucydides.core.requirements;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.reports.FileSystemRequirmentsOutcomeFactory;
import net.thucydides.core.requirements.reports.RequirementsOutcomeFactory;

import java.util.List;

import static net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;

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
