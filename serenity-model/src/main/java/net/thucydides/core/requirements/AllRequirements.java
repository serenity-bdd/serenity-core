package net.thucydides.core.requirements;

import net.thucydides.core.requirements.model.Requirement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class AllRequirements {

    public static Stream<Requirement> asStreamFrom(List<Requirement> requirements) {
        if (requirements == null) {
            return Stream.of();
        }
        return requirements.stream().flatMap(Requirement::stream);
    }

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

    private static Collection<Requirement> childRequirementsOf(Requirement requirement) {
        List<Requirement> childRequirements = new ArrayList<>();

        requirement.getChildrenAsStream().forEach(
                childRequirement -> {
                    childRequirements.add(childRequirement);
                    childRequirements.addAll(childRequirementsOf(childRequirement));
                }
        );
//
//        for (Requirement childRequirement : requirement.getChildren()) {
//            childRequirements.add(childRequirement);
//            childRequirements.addAll(childRequirementsOf(childRequirement));
//        }
        return childRequirements;
    }
}
