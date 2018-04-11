package net.thucydides.core.requirements;

import net.thucydides.core.requirements.model.Requirement;

import java.util.ArrayList;
import java.util.List;

public class RequirementsTree {
    private final Requirement root;

    public RequirementsTree(Requirement root) {

        this.root = root;
    }

    public static RequirementsTree forRequirement(Requirement root) {
        return new RequirementsTree(root);
    }

    public List<Requirement> asFlattenedList() {
        List<Requirement> requirements = new ArrayList<>();
        requirements.add(root);
        for(Requirement child : root.getChildren()) {
            requirements.addAll(RequirementsTree.forRequirement(child).asFlattenedList());
        }
        return requirements;
    }

}
