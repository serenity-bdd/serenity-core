package serenitymodel.net.thucydides.core.requirements;

import serenitymodel.net.thucydides.core.requirements.reports.RequirementsOutcomeFactory;

import java.util.List;

public interface Requirements {
    RequirementsService getRequirementsService();

    RequirementsOutcomeFactory getRequirementsOutcomeFactory();

    List<String> getTypes();
}
