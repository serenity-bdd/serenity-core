package net.thucydides.model.requirements;

import net.thucydides.model.requirements.model.Requirement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class RequirementAncestry {

    public static void addParentsTo(List<Requirement> requirements) {
        addParentsTo(requirements, null);
    }

    public static void addParentsTo(List<Requirement> requirements, String parent) {
        for(Requirement requirement : requirements) {
            addParentsTo(requirement.getChildren(), requirement.getId());
            requirement.setParent(parent);
        }
    }
}
