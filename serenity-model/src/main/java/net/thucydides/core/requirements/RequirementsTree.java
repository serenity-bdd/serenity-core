package net.thucydides.core.requirements;

import net.thucydides.core.requirements.model.Requirement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class RequirementsTree {
    private final List<Requirement> rootRequirements;

    private RequirementsTree(Requirement root) {
        this.rootRequirements = Collections.singletonList(root);
    }

    private RequirementsTree(List<Requirement> requirements) {
        this.rootRequirements = requirements;
    }

    public static RequirementsTree forRequirement(Requirement root) {
        return new RequirementsTree(root);
    }

    public static RequirementsTree from(List<Requirement> requirements) {
        return new RequirementsTree(requirements);
    }

    public Stream<Requirement> stream() {
        return rootRequirements.stream().flatMap(Requirement::stream);
    }

    public List<Requirement> asFlattenedList() {
        List<Requirement> requirements = new ArrayList<>();
        rootRequirements.forEach(
                requirement -> {
                    requirements.add(requirement);
                    for(Requirement child : requirement.getChildren()) {
                        requirements.addAll(RequirementsTree.forRequirement(child).asFlattenedList());
                    }
                }
        );
        return requirements;
    }

    public String toString() {
        return net.thucydides.core.requirements.model.RequirementTree.withRequirements(rootRequirements).toString();
    }

}
