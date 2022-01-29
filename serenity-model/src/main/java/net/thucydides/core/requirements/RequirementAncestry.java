package net.thucydides.core.requirements;

import net.thucydides.core.requirements.model.Requirement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class RequirementAncestry {

    private static final List<Requirement> NO_REQUIREMENTS = Collections.synchronizedList(new ArrayList<>());

    public static List<Requirement> addParentsTo(List<Requirement> requirements) {
        return addParentsTo(requirements, null);
    }

    public static List<Requirement> addParentsTo(List<Requirement> requirements, String parent) {
        List<Requirement> augmentedRequirements = new ArrayList<>();
        for(Requirement requirement : requirements) {
            List<Requirement> children = requirement.hasChildren()
                    ? addParentsTo(requirement.getChildren(),requirement.getName()) : NO_REQUIREMENTS;
            augmentedRequirements.add(requirement.withParent(parent).withChildren(children));
        }
        //return unmodifiableList(augmentedRequirements);
        return augmentedRequirements;
    }

}
