package net.thucydides.model.requirements;

import net.thucydides.model.requirements.model.Requirement;

import java.util.List;

public class RequirementAncestry {

    public static void addParentsTo(List<Requirement> requirements) {
        addParentsTo(requirements, "");
    }

    public static void addParentsTo(List<Requirement> requirements, String parent) {
        for(Requirement requirement : requirements) {
            addParentsTo(requirement.getChildren(), requirement.getId());
            requirement.setParent(parent);
        }
    }
}
