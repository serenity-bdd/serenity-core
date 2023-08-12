package net.thucydides.model.requirements.classpath;

import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.serenitybdd.model.strings.Joiner;
import net.thucydides.model.domain.AnnotatedStoryTitle;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.requirements.model.RequirementTypeAt;
import net.thucydides.model.requirements.model.RequirementsConfiguration;

import java.util.*;

import static net.thucydides.model.requirements.classpath.PathElements.*;
import static net.thucydides.model.util.NameConverter.humanize;

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

        String displayName = displayNameFor(path).orElse(storyName);

        Requirement story = Requirement.named(storyName)
                .withType(narrativeType)
                .withNarrative(narrativeText)
                .withParent(parent)
                .withDisplayName(displayName)
                .withTags(storyTags)
                .withScenarioTags(testTags)
                .withPath(Joiner.on("/").join(allButLast(pathElements)));

        return story;
    }

    private Optional<String> displayNameFor(String path) {
        try {
            return AnnotatedStoryTitle.forClass(Class.forName(path));
        } catch (ClassNotFoundException tryWithInnerClasses) {
            List<Integer> dotIndexes = new ArrayList<>();
            for (int i = 0; i < path.length(); i++) {
                if (path.charAt(i) == '.') {
                    dotIndexes.add(i);
                }
            }
            StringBuilder updatedPath = new StringBuilder(path);
            for (int index = dotIndexes.size() - 1; index >= 0; index--) {
                updatedPath.setCharAt(dotIndexes.get(index), '$');
                try {
                    return AnnotatedStoryTitle.forClass(Class.forName(updatedPath.toString()));
                } catch (ClassNotFoundException noMatchingInnerClass) {
                    // Continue with next level of nesting
                }
            }
            return Optional.empty();
        }
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
        for (Requirement requirement : knownRequirements) {
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
