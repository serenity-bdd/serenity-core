package net.thucydides.core.requirements.classpath;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementsConfiguration;

import java.util.Collection;
import java.util.List;

import static net.thucydides.core.requirements.classpath.ChildElementAdder.addChild;
import static net.thucydides.core.requirements.classpath.PathElements.allButLast;
import static net.thucydides.core.requirements.classpath.PathElements.elementsOf;
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
        this.requirementsConfiguration = new RequirementsConfiguration(ConfiguredEnvironment.getEnvironmentVariables());
        this.requirementsDepth = requirementsDepth;
    }

    public void to(Collection<Requirement> allRequirements) {

        List<String> parentElements =
                PackageInfoClass.isDefinedIn(path) ?
                        allButLast(allButLast(elementsOf(path, rootPackage))) :
                        allButLast(elementsOf(path, rootPackage));

        int startFromRequirementLevel = getRequirementTypes().size() - requirementsDepth;

        int level = startFromRequirementLevel;

        Requirement parent = null;

        StringBuilder fullPath = new StringBuilder(rootPackage);
        for (String pathElement : parentElements) {

            String type = requirementsConfiguration.getRequirementType(level++);

            fullPath.append(".").append(pathElement);

            Requirement nextRequirement;

            String parentName = (parent != null) ? parent.getName() : null;

            if (requirementExistsCalled(humanize(pathElement), allRequirements)) {
                nextRequirement = requirementCalled(humanize(pathElement), allRequirements).withParent(parentName);
            } else {
                String narrativeText = PackageInfoNarrative.text().definedInPath(fullPath.toString()).or(NarrativeText.definedIn(fullPath.toString(), type));
                String narrativeType = PackageInfoNarrative.type().definedInPath(fullPath.toString()).or(type);
                nextRequirement = (Requirement.named(humanize(pathElement)).withType(narrativeType).withNarrative(narrativeText)).withParent(parentName);
                allRequirements.add(nextRequirement);
            }
            if (parent != null) {
                addChild(nextRequirement).toParent(parent).in(allRequirements);
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
        for (Requirement requirement : allRequirements) {
            if (requirement.getName().equals(name)) {
                return requirement;
            }
        }
        return null;
    }


    public static NonLeafRequirementsAdderBuilder addParentsOf(Requirement leafRequirement) {
        return new NonLeafRequirementsAdderBuilder(leafRequirement);
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
