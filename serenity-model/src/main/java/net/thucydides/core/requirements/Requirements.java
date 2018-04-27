package net.thucydides.core.requirements;

import net.thucydides.core.requirements.reports.RequirementsOutcomeFactory;

import java.util.List;

public interface Requirements {
    RequirementsService getRequirementsService();

    RequirementsOutcomeFactory getRequirementsOutcomeFactory();

    List<String> getTypes();
}
