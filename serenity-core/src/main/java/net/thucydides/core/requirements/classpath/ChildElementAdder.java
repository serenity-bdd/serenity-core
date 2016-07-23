package net.thucydides.core.requirements.classpath;

import net.thucydides.core.requirements.model.Requirement;

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
        requirements.remove(parent);
        requirements.add(parent.withChild(child));
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
