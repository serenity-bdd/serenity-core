package net.thucydides.core.requirements;

import com.google.common.base.Splitter;
import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.*;
import net.thucydides.core.reports.TestOutcomeLoader;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static net.thucydides.core.guice.Injectors.getInjector;

/**
 * Read requirements from test outcomes, based on the path specified in each test outcome
 */
public class TestOutcomeRequirementsTagProvider implements RequirementsTagProvider, OverridableTagProvider, RequirementTypesProvider {

    private final RequirementsConfiguration requirementsConfiguration;
    private EnvironmentVariables environmentVariables;
    private final String testRoot;

    private List<Requirement> requirements;
    private Map<Requirement, Requirement> requirementAliases;
    private List<Requirement> flattenedRequirements;

    private final static List<String> SUPPORTED_TEST_SOURCES = NewList.of("JUnit", "JUnit5");

    public TestOutcomeRequirementsTagProvider() {
        this(getInjector().getProvider(EnvironmentVariables.class).get());
    }

    public TestOutcomeRequirementsTagProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.requirementsConfiguration = new RequirementsConfiguration(environmentVariables);
        this.testRoot = ThucydidesSystemProperty.SERENITY_TEST_ROOT.from(environmentVariables, "");
    }

    @Override
    public List<String> getActiveRequirementTypes() {

        Map<String, Integer> requirementsDepth = new HashMap<>();
        getFlattenedRequirements().forEach(
                requirement -> {
                    if (requirementsDepth.getOrDefault(requirement.getType(), 0) < requirement.getDepth()) {
                        requirementsDepth.put(requirement.getType(), requirement.getDepth());
                    }
                }
        );

        // List the requirement types in order of depth
        return requirementsDepth.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<Requirement> getRequirements() {
        if (requirements == null) {
            synchronized (this) {
                requirements = loadRequirements();
            }
        }
        return requirements;
    }

    private Stream<TestOutcome> supportedOutcomesFrom(List<TestOutcome> outcomes) {
        return outcomes.stream()
                .filter(outcome -> (outcome.getTestSource() == null) || SUPPORTED_TEST_SOURCES.contains(outcome.getTestSource()));
    }

    private List<Requirement> loadRequirements() {
        TestOutcomeLoader loader = new TestOutcomeLoader();
        File outputDirectory = new File(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.from(environmentVariables, "target/site/serenity"));
        List<TestOutcome> outcomes = loader.loadFrom(outputDirectory);
        List<String> rootElements = rootPackageElements();

        int maxRequirementsDepth = supportedOutcomesFrom(outcomes)
                .filter(outcome -> !outcome.getUserStory().getParentPathElements().isEmpty())
                .mapToInt(outcome -> outcome.getUserStory().getParentPathElements().size())// - 1)
                .max()
                .orElse(0);

        // Bottom-level requirements
        List<Requirement> leafLevelRequirements = supportedOutcomesFrom(outcomes)
                .map(TestOutcome::getUserStory)
//                .map(userStory -> stripeRootFrom(userStory,rootElements))
                .distinct()
                .map(this::requirementFrom)
                .collect(Collectors.toList());

        // Non-leaf requirements indexed by path
        Map<PathElements, Requirement> nonLeafRequirements = supportedOutcomesFrom(outcomes)
                .map(outcome -> outcome.getUserStory().getParentPathElements())
                .filter(pathElements -> !pathElements.isEmpty())
                .distinct()
                .map(pathElements -> relativePathFrom(pathElements))
                .filter(pathElements -> !pathElements.isEmpty())
                .collect(toMap(pathElements -> pathElements,
                        pathElements -> nonLeafRequirementFrom(pathElements, maxRequirementsDepth)));

        // Use the map to update the parent requirements
        nonLeafRequirements.forEach((path, requirement) -> {
            PathElements parentPath = path.getParent();
            if (parentPath != null) {
                Requirement parentRequirement = nonLeafRequirements.get(parentPath);
                if (parentRequirement != null) {
                    requirement.setParent(parentRequirement.getPath());
                }
            }
        });

        // Use the map to update the leaf requirements
        leafLevelRequirements.forEach(
                requirement -> {
                    PathElements parentPath = requirement.getParentPathElements(environmentVariables);
                    if (parentPath != null && !parentPath.isEmpty()) {
                        Requirement parentRequirement = nonLeafRequirements.get(parentPath);
                        if (parentRequirement != null) {
                            requirement.setParent(parentRequirement.getPath());
                        }
                    }
                }
        );

        // Make an alias for any leaf requirements that also appear in the non-leaf requirements.

        this.requirementAliases = leafLevelRequirements.stream()
                .filter(requirement -> nonLeafRequirements.containsKey(requirement.getPathElements()))
                .collect(Collectors.toMap(
                        requirement -> requirement,
                        requirement -> nonLeafRequirements.get(requirement.getPathElements())));


        // Remove any aliased requirements from the leaf requirements list
        leafLevelRequirements.removeAll(requirementAliases.keySet());

        // Associate child requirements with each non-leaf requirement, including both non-leaf and leaf requirements
        nonLeafRequirements.forEach((path, requirement) -> {
            // Find leaf requirements that are children of this requirement
            List<Requirement> childRequirements = leafLevelRequirements.stream()
                    .filter(childRequirement -> childRequirement.hasParent(path))
                    .collect(Collectors.toList());
            // Find other non-leaf requirements that are children of this requirement
            List<Requirement> nonLeafChildRequirements = nonLeafRequirements.values().stream()
                    .filter(childRequirement -> childRequirement.hasParent(path))
                    .collect(Collectors.toList());
            childRequirements.addAll(nonLeafChildRequirements);
            // The list of children is now complete
            requirement.setChildren(childRequirements);
        });

        // Return a list of the top-level or leaf requirements with no parent elements
        return Stream.concat(nonLeafRequirements.values().stream(), leafLevelRequirements.stream())
                .filter(requirement -> requirement.getParent() == null)
                .collect(Collectors.toList());
    }

    /*
       Max Depth = 4
       + sample theme                                   0 -> theme
         + nested capability                            1 -> capability
           + deep - feature                             2 -> feature
             + deeper - feature                         3 -> feature
                + ADeeperNestedStory.java  - story
             + ADeepNestedStory.java  - story

         Max Depth = 3
         + nested                                       0 -> theme
           + deep                                       1 -> capability
             + deeper                                   2 -> feature
                + ADeeperNestedStory.java  - story
             + ADeepNestedStory.java  - story

         Max Depth = 2
         + nested                                       0 -> capability
             + deep                                     1 -> feature
                + ADeeperNestedStory.java  - story
             + ADeepNestedStory.java  - story

     */
    private Requirement nonLeafRequirementFrom(PathElements relativePath, int maxRequirementsDepth) {
        PathElement requirementLeaf = relativePath.get(relativePath.size() - 1);
        String path = relativePath.stream()
                .map(PathElement::getName)
                .collect(Collectors.joining("/"));

        String requirementType = requirementsConfiguration.getRequirementType(relativePath.size() - 1, maxRequirementsDepth);
        String requirementName = Inflector.inflection().humanize(requirementLeaf.getName());
        return Requirement.named(requirementName)
                .withType(requirementType)
                .withNarrative(requirementLeaf.getDescription())
                .withPath(path);
    }

    private PathElements relativePathFrom(PathElements pathElements) {
        List<String> rootPackageElements = rootPackageElements();
        List<PathElement> relativePathElements = new ArrayList<>(pathElements);
        for (String rootPackageElement : rootPackageElements) {
            if (relativePathElements.get(0).getName().equals(rootPackageElement)) {
                relativePathElements.remove(0);
            }
        }
        return PathElements.from(relativePathElements);
    }

    @NotNull
    private List<String> rootPackageElements() {
        String rootPackage = ThucydidesSystemProperty.SERENITY_TEST_ROOT.from(environmentVariables, "");
        List<String> rootPackageElements = Splitter.on(".").omitEmptyStrings().splitToList(rootPackage);
        return rootPackageElements;
    }

    private Requirement requirementFrom(Story userStory) {
        List<String> rootPackageElements = rootPackageElements();

        return Requirement.named(userStory.getName()).withType(userStory.getType())
                .withNarrative(userStory.getNarrative())
                .withPath(userStory.getPath());
    }

    @Override
    public Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome) {
        if ((testOutcome.getUserStory() == null) || (testOutcome.getUserStory().getPath() == null)) {
            return Optional.empty();
        }
        Optional<Requirement> firstChoice = getFlattenedRequirements().stream()
                .filter(requirement -> requirement.matchesUserStory(testOutcome.getUserStory()))
                .findFirst();

        Optional<Requirement> aliasedRequirement = Optional.empty();
        if (!firstChoice.isPresent()) {
            aliasedRequirement = requirementAliases.keySet().stream()
                    .filter(requirement -> requirement.matchesUserStory(testOutcome.getUserStory()))
                    .map(requirement -> requirementAliases.get(requirement))
                    .findFirst();
        }
        return firstChoice.isPresent() ? firstChoice : aliasedRequirement;
//
//        testOutcome.getUserStory().getPathElements();
//
//        String testOutcomePath = testOutcome.getUserStory().getPath();
//        String normalizedPath = testOutcomePath.replace(".", "/");
//        return getFlattenedRequirements().stream()
//                .filter(requirement -> testOutcomePath.equals(requirement.getPath()) || normalizedPath.equals(requirement.getPath()))
//                .findFirst();
    }

    @Override
    public Optional<Requirement> getParentRequirementOf(Requirement requirement) {
        // find the requirement with the same path as the parent of this requirement
        return getFlattenedRequirements().stream()
                .filter(parentRequirement -> requirement.hasParent(parentRequirement.getPath()))
                .findFirst();
    }

    @Override
    public java.util.Optional<Requirement> getRequirementFor(TestTag testTag) {
        java.util.Optional<Requirement> result = Optional.empty();
        for (Requirement requirement : getFlattenedRequirements()) {
            if (requirement.matchesTag(testTag)) {
                return Optional.of(requirement);
            }
        }
        return result;
    }

    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        Set<TestTag> result = new HashSet<>();

        Optional<Requirement> parentRequirement = getParentRequirementOf(testOutcome);

        if (parentRequirement.isPresent()) {
            result.add(parentRequirement.get().asTag());

            Optional<Requirement> parent = getParentRequirementOf(parentRequirement.get());
            while (parent.isPresent()) {
                result.add(parent.get().asTag());
                parent = getParentRequirementOf(parent.get());
            }
        }
        return result;
    }

    private boolean isMatchingRequirementFor(TestOutcome testOutcome, Requirement requirement) {
        String testOutcomePath = testOutcome.getUserStory().getPath();
        return requirement.matchesTag(testOutcome.getUserStory().asTag())
                || requirement.matchesOrIsAParentOf(testOutcomePath);
    }

    public List<Requirement> getFlattenedRequirements() {
        return getFlattenedRequirements(getRequirements());
    }

    private List<Requirement> getFlattenedRequirements(List<Requirement> requirements) {
        if (this.flattenedRequirements == null) {
            List<Requirement> flattenedRequirements = new ArrayList<>();
            for (Requirement requirement : requirements) {
                flattenedRequirements.add(requirement);
                if (requirement.getChildren() != null && !requirement.getChildren().isEmpty()) {
                    flattenedRequirements.addAll(getFlattenedRequirements(requirement.getChildren()));
                }
            }
            this.flattenedRequirements = flattenedRequirements;
        }
        return this.flattenedRequirements;
    }
}
