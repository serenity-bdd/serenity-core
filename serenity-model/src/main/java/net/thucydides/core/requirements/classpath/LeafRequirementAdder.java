package net.thucydides.core.requirements.classpath;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.strings.Joiner;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementTypeAt;
import net.thucydides.core.requirements.model.RequirementsConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.thucydides.core.requirements.classpath.PathElements.*;
import static net.thucydides.core.util.NameConverter.humanize;

public class LeafRequirementAdder {

    private final String path;
    private final String rootPackage;
    private final int requirementsDepth;
    private List<String> activeRequirementTypes;

    RequirementsConfiguration requirementsConfiguration;

    public static LeafRequirementsAdderBuilder addLeafRequirementDefinedIn(String path) {
        return new LeafRequirementsAdderBuilder(path);
    }

    public static class LeafRequirementsAdderBuilder {

        private final String path;
        private int requirementsDepth;
        private List<String> activeRequirementTypes;

        public LeafRequirementsAdderBuilder(String path) {

            this.path = path;
        }

        public LeafRequirementAdder startingAt(String rootPackage) {
            return new LeafRequirementAdder(path, requirementsDepth, rootPackage, activeRequirementTypes);
        }

        public LeafRequirementsAdderBuilder withAMaximumRequirementsDepthOf(int requirementsDepth) {
            this.requirementsDepth = requirementsDepth;
            return this;
        }

        public LeafRequirementsAdderBuilder usingRequirementTypes(List<String> activeRequirementTypes) {
            this.activeRequirementTypes = activeRequirementTypes;
            return this;
        }
    }

    protected LeafRequirementAdder(String path, int requirementsDepth, String rootPackage, List<String> activeRequirementTypes) {
        this.path = path;
        this.rootPackage = rootPackage;
        this.requirementsDepth = requirementsDepth;
        this.activeRequirementTypes = activeRequirementTypes;
        this.requirementsConfiguration = new RequirementsConfiguration(ConfiguredEnvironment.getEnvironmentVariables());
    }


    public Requirement to(Collection<Requirement> allRequirements) {

        Requirement newRequirement = PackageInfoClass.isDefinedIn(path) ?
                requirementDefinedByNarrativeAt(path, allRequirements)
                : requirementDefinedByClassNameAt(path);

        allRequirements.add(newRequirement);

        return newRequirement;
    }

    private Requirement requirementDefinedByClassNameAt(String path) {

        List<String> pathElements = elementsOf(path, rootPackage);
        String storyName = humanize(lastOf(pathElements));

        String parent = null;
        if (pathElements.size() >= 2) {
            parent = humanize(secondLastOf(pathElements));
        }

        String narrativeText = PackageInfoNarrative.text().definedInPath(path)
                .orElse(ClassNarrative.text().definedInPath(path).orElse(""));

        String narrativeType = PackageInfoNarrative.type().definedInPath(path)
                .orElse(ClassNarrative.type().definedInPath(path).orElse(leafRequirementTypeFrom(pathElements)));

        List<TestTag> storyTags = AnnotatedTags.forClassDefinedInPath(path);
        Map<String, Collection<TestTag>> testTags = AnnotatedTags.forTestMethodsDefinedInPath(path);
        Requirement story = Requirement.named(storyName)
                .withType(narrativeType)
                .withNarrative(narrativeText)
                .withParent(parent)
                .withTags(storyTags)
                .withScenarioTags(testTags)
                .withPath(Joiner.on("/").join(allButLast(pathElements)));

        return story;
    }

    private Requirement requirementDefinedByNarrativeAt(String path, Collection<Requirement> knownRequirements) {

        List<String> pathElements = elementsOf(path, rootPackage);
        List<String> featurePathElements = allButLast(pathElements);
        String featureName = humanize(lastOf(featurePathElements));

        String parent = null;
        if (featurePathElements.size() >= 2) {
            parent = humanize(secondLastOf(featurePathElements));
        }

        int startFromRequirementLevel = requirementsConfiguration.startLevelForADepthOf(requirementsDepth);

        String typeByLevel = requirementsConfiguration.getRequirementType(startFromRequirementLevel + featurePathElements.size() - 1);
        String type = PackageInfoNarrative.type().definedInPath(path).orElse(typeByLevel);

        Optional<Requirement> knownMatchingRequirement = findMatchingRequirementWithName(knownRequirements, featureName, type);

        if (knownMatchingRequirement.isPresent()) {
            knownRequirements.remove(knownMatchingRequirement);
        }
        return knownMatchingRequirement.orElse(Requirement.named(featureName).withTypeOf(type))
                .withNarrative(PackageInfoNarrative.text().definedInPath(path).orElse(""))
                .withParent(parent)
                .withPath(Joiner.on("/").join(featurePathElements));
    }

    private Optional<Requirement> findMatchingRequirementWithName(Collection<Requirement> knownRequirements, String featureName, String featureType) {
        for(Requirement requirement : knownRequirements) {
            if (requirement.getName().equalsIgnoreCase(featureName) && requirement.getType().equalsIgnoreCase(featureType)) {
                return Optional.of(requirement);
            }
        }
        return Optional.empty();
    }

    private String leafRequirementTypeFrom(List<String> pathElements) {
        return RequirementTypeAt.level(pathElements.size() - 1).in(activeRequirementTypes);
    }
}