package net.thucydides.model.requirements.classpath;

import net.thucydides.model.requirements.model.Requirement;

import java.util.Collection;

/**
 * Created by john on 13/07/2016.
 */
public class ChildElementAdder {
    private final Requirement parent;
    private final Requirement child;

    public ChildElementAdder(Requirement parent, Requirement child) {
        this.parent = parent;
        this.child = child;
    }

    public static ChildElementAdderBuilder addChild(Requirement requirement) {
        return new ChildElementAdderBuilder(requirement);
    }

    public void in(Collection<Requirement> requirements) {

        Requirement ancestor = parent;
        Requirement immediateDescendant = child;

        while(ancestor != null) {
            Requirement updatedAncestor = ancestor.withChild(immediateDescendant);
            replaceIn(requirements, ancestor, updatedAncestor);
            immediateDescendant = updatedAncestor;
            ancestor = requirementCalled(ancestor.getParent(), requirements);
        }
    }

    private Requirement requirementCalled(String requirementName, Collection<Requirement> requirements) {
        for(Requirement requirement : requirements) {
            if (requirement.getName().equals(requirementName)) {
                return requirement;
            }
        }
        return null;
    }

    private void replaceIn(Collection<Requirement> requirements, Requirement oldParent, Requirement updatedParent) {
        requirements.remove(oldParent);
        requirements.add(updatedParent);
    }

    public static class ChildElementAdderBuilder{

        private Requirement child;

        public ChildElementAdderBuilder(Requirement child) {

            this.child = child;
        }

        public ChildElementAdder toParent(Requirement parent) {
            return new ChildElementAdder(parent, child);
        }
    }
}
