package net.thucydides.core.requirements;

import net.serenitybdd.core.di.ModelInfrastructure;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.reports.MultipleSourceRequirmentsOutcomeFactory;
import net.thucydides.core.requirements.reports.RequirementsOutcomeFactory;

import java.util.List;

import static net.thucydides.core.reports.html.ReportNameProvider.NO_CONTEXT;

/**
 * Created by john on 26/06/2016.
 */
public class DefaultRequirements implements Requirements {

    private final RequirementsService requirementsService;
    private final RequirementsOutcomeFactory requirmentsOutcomeFactory;

    public DefaultRequirements(String testRootPackage) {
        if (testRootPackage != null) {
            ConfiguredEnvironment.getEnvironmentVariables().setProperty("serenity.test.root", testRootPackage);
        }
        this.requirementsService = ModelInfrastructure.getRequirementsService();
        this.requirmentsOutcomeFactory = new MultipleSourceRequirmentsOutcomeFactory(
                ModelInfrastructure.getRequirementsProviderService().getRequirementsProviders(),
                ModelInfrastructure.getIssueTracking(),
                new ReportNameProvider(NO_CONTEXT, ReportType.HTML, getRequirementsService()));
    }

    public DefaultRequirements() {
        this.requirementsService = ModelInfrastructure.getRequirementsService();
        
        this.requirmentsOutcomeFactory = new MultipleSourceRequirmentsOutcomeFactory(
                ModelInfrastructure.getRequirementsProviderService().getRequirementsProviders(),
                ModelInfrastructure.getIssueTracking(),
                new ReportNameProvider(NO_CONTEXT, ReportType.HTML, getRequirementsService()));
    }

    public DefaultRequirements(ReportNameProvider reportNameProvider) {
        this.requirementsService = ModelInfrastructure.getRequirementsService();
        this.requirmentsOutcomeFactory = new MultipleSourceRequirmentsOutcomeFactory(
                ModelInfrastructure.getRequirementsProviderService().getRequirementsProviders(),
                ModelInfrastructure.getIssueTracking(),
                reportNameProvider);
    }

    public RequirementsService getRequirementsService() {
        return requirementsService;
    }

    @Override
    public RequirementsOutcomeFactory getRequirementsOutcomeFactory() {
        return requirmentsOutcomeFactory;
    }

    public List<String> getTypes() {
        return requirementsService.getRequirementTypes();
    }

}
