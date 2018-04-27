package net.thucydides.core.requirements;

import net.thucydides.core.requirements.model.Requirement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AllRequirements {

    public static List<Requirement> in(List<Requirement> requirements) {
        if (requirements == null) {
            return new ArrayList<>();
        }

        List<Requirement> allRequirements = new ArrayList<>();
        for (Requirement requirement : requirements) {
            allRequirements.add(requirement);
            allRequirements.addAll(childRequirementsOf(requirement));
        }
        return allRequirements;
    }

    protected static Collection<Requirement> childRequirementsOf(Requirement requirement) {
        List<Requirement> childRequirements = new ArrayList<>();
        for (Requirement childRequirement : requirement.getChildren()) {
            childRequirements.add(childRequirement);
            childRequirements.addAll(childRequirementsOf(childRequirement));
        }
        return childRequirements;
    }
}
