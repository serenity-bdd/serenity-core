package net.thucydides.core.requirements;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Optional;
import net.thucydides.core.requirements.model.Requirement;

import java.util.List;

import static java.lang.Math.max;

/**
 * Provide aggregate information about a list of requirements
 */
public class RequirementsList {

    private final List<Requirement> requirements;

    protected RequirementsList(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public static RequirementsList of(List<Requirement> requirements) {
        return new RequirementsList(requirements);
    }

    protected int maxDepth() {
        if (requirements.isEmpty()) { return 0; }

        int maxDepth = 1;
        for(Requirement requirement : requirements) {
            maxDepth = max(1 + RequirementsList.of(requirement.getChildren()).maxDepth(), maxDepth);
        }
        return maxDepth;
    }

    public List<Requirement> asFlattenedList() {
        List<Requirement> flattenedRequirements = Lists.newArrayList();
        for(Requirement requirement : requirements) {
            flattenedRequirements.add(requirement);
            if (requirement.hasChildren()) {
                flattenedRequirements.addAll(RequirementsList.of(requirement.getChildren()).asFlattenedList());
            }
        }
        return flattenedRequirements;
    }

    public Optional<Requirement> findByUniqueName(String name) {
        Requirement matchingRequirement = null;
        for(Requirement requirement : asFlattenedList()) {
            if (requirement.getName().equalsIgnoreCase(name)) {
                if (matchingRequirement != null) {
                    return Optional.absent();
                }
                matchingRequirement = requirement;
            }
        }
        return Optional.fromNullable(matchingRequirement);
    }
}
