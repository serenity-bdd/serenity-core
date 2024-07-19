package net.thucydides.model.requirements;

import net.thucydides.model.requirements.reports.RequirementsOutcomeFactory;

import java.util.List;

public interface Requirements {
    RequirementsService getRequirementsService();

    RequirementsOutcomeFactory getRequirementsOutcomeFactory();

    List<String> getTypes();
}
