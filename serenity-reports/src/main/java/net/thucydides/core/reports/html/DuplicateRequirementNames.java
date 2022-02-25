package net.thucydides.core.reports.html;

import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;

public class DuplicateRequirementNames {
    public static boolean presentIn(RequirementsOutcomes filteredRequirementsOutcomes) {
        long uniqueRequirementNames = filteredRequirementsOutcomes.getRequirements()
                .stream()
                .map(Requirement::getDisplayName)
                .distinct()
                .count();
        return uniqueRequirementNames < filteredRequirementsOutcomes.getRequirements().size();
    }

}
