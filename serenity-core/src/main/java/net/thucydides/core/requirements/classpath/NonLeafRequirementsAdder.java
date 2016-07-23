package net.thucydides.core.requirements.classpath;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.Collection;
import java.util.List;

import static net.thucydides.core.requirements.classpath.ChildElementAdder.addChild;
import static net.thucydides.core.requirements.classpath.PathElements.*;
import static net.thucydides.core.util.NameConverter.humanize;

/**
 * Created by john on 13/07/2016.
 */
public class NonLeafRequirementsAdder {
    private final String path;
    private final Requirement leafRequirement;
    private final String rootPackage;
    private final int requirementsDepth;
    protected final RequirementsConfiguration requirementsConfiguration;

    public NonLeafRequirementsAdder(String path, String rootPackage, int requirementsDepth, Requirement leafRequirement) {
        this.path = path;
        this.rootPackage = rootPackage;
        this.leafRequirement = leafRequirement;
        this.requirementsConfiguration = new RequirementsConfiguration(Injectors.getInjector().getInstance(EnvironmentVariables.class));
        this.requirementsDepth = requirementsDepth;
    }

    public void to(Collection<Requirement> allRequirements) {

        List<String> parentElements = allButLast(elementsOf(path, rootPackage));

        int startFromRequirementLevel = getRequirementTypes().size() - requirementsDepth;

        List<String> requirementTypes = allButLast(getRequirementTypes());

        String fullPath = rootPackage;
        int level = startFromRequirementLevel;

        Requirement parent = null;
        for (String pathElement : parentElements) {

            String type = getRequirementType(requirementTypes, level++);

            fullPath = fullPath + "." + pathElement;

            Requirement nextRequirement;

            String parentName = (parent != null) ? parent.getName() : null;

            if (requirementExistsCalled(humanize(pathElement),allRequirements)) {
                nextRequirement = requirementCalled(humanize(pathElement), allRequirements).withParent(parentName);
                if (parent != null) {
                    addChild(nextRequirement).toParent(parent).in(allRequirements);
                }
            } else {
                String narrativeText = NarrativeText.definedIn(fullPath, type);
                nextRequirement = (Requirement.named(humanize(pathElement)).withType(type).withNarrative(narrativeText)).withParent(parentName);
                allRequirements.add(nextRequirement);
            }
            parent = nextRequirement;
        }

        addChild(leafRequirement).toParent(parent).in(allRequirements);
    }

    protected List<String> getRequirementTypes() {
        return requirementsConfiguration.getRequirementTypes();
    }

    private boolean requirementExistsCalled(String name, Collection<Requirement> allRequirements) {
        return requirementCalled(name, allRequirements) != null;
    }

    private Requirement requirementCalled(String name, Collection<Requirement> allRequirements) {
        for(Requirement requirement : allRequirements) {
            if (requirement.getName().equals(name)) {
                return requirement;
            }
        }
        return null;
    }


    public static NonLeafRequirementsAdderBuilder addParentsOf(Requirement leafRequirement) {
        return new NonLeafRequirementsAdderBuilder(leafRequirement);
    }

    String getRequirementType(List<String> types, int level) {
        if (level >= types.size()) {
            return lastOf(types);
        } else {
            return types.get(level);
        }
    }


    public static class NonLeafRequirementsAdderBuilder {

        private Requirement leafRequirement;
        private String path;
        private int requirementsDepth = 0;

        public NonLeafRequirementsAdderBuilder(Requirement leafRequirement) {
            this.leafRequirement = leafRequirement;
        }

        public NonLeafRequirementsAdder startingAt(String rootPackage) {
            return new NonLeafRequirementsAdder(path, rootPackage, requirementsDepth, leafRequirement);
        }

        public NonLeafRequirementsAdderBuilder in(String path) {
            this.path = path;
            return this;
        }

        public NonLeafRequirementsAdderBuilder withAMaximumRequirementsDepthOf(int requirementsDepth) {
            this.requirementsDepth = requirementsDepth;
            return this;
        }
    }
}
