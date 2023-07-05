package net.thucydides.core.requirements;

import com.google.common.base.Splitter;
import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.*;
import net.thucydides.core.reports.TestOutcomeLoader;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
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
    private final EnvironmentVariables environmentVariables;

    private final static List<String> SUPPORTED_TEST_SOURCES = NewList.of("JUnit", "JUnit5");

    public TestOutcomeRequirementsTagProvider() {
        this(getInjector().getProvider(EnvironmentVariables.class).get());
    }

    public TestOutcomeRequirementsTagProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.requirementsConfiguration = new RequirementsConfiguration(environmentVariables);
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
        return RequirementCache.getInstance().getRequirements(this::loadRequirements);
    }

    private Stream<TestOutcome> supportedOutcomesFrom(List<TestOutcome> outcomes) {
        return outcomes.stream()
                .filter(outcome -> (outcome.getTestSource() == null) || SUPPORTED_TEST_SOURCES.contains(outcome.getTestSource()));
    }

    private List<Requirement> loadRequirements() {
        TestOutcomeLoader loader = new TestOutcomeLoader();
        File outputDirectory = new File(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.from(environmentVariables, "target/site/serenity"));
        List<TestOutcome> outcomes = loader.loadFrom(outputDirectory);
        Map<Requirement, Requirement> requirementAliases = new HashMap<>();

        int maxRequirementsDepth = supportedOutcomesFrom(outcomes)
                .filter(outcome -> !outcome.getUserStory().getParentPathElements().isEmpty())
                .mapToInt(outcome -> outcome.getUserStory().getParentPathElements().size())// - 1)
                .max()
                .orElse(0);

        // Bottom-level requirements
        Map<PathElements, Requirement> leafLevelRequirements = supportedOutcomesFrom(outcomes)
                .map(TestOutcome::getUserStory)
                .map(this::requirementFrom)
                .distinct()
                .collect(Collectors.toMap(
                        Requirement::getPathElements,
                        requirement -> requirement
                ));

        // Record intermediate display names by path for later use
        supportedOutcomesFrom(outcomes)
                .map(outcome -> outcome.getUserStory().getPathElements())
                .forEach(path -> recordDisplayNameFor(path));

        List<PathElements> pathElementsInTestOutcomes = supportedOutcomesFrom(outcomes)
                .map(outcome -> outcome.getUserStory().getPathElements())
                .distinct()
                .map(this::relativePathFrom)
                .filter(pathElements -> !pathElements.isEmpty())
                .collect(Collectors.toList());

        Set<PathElements> leafPathElements = leafLevelRequirements.keySet();

        Map<PathElements, Requirement> requirementsByPath = new HashMap<>();

        // Non-leaf requirements indexed by path
        pathElementsInTestOutcomes.forEach(pathElements -> {
            Requirement requirement = nonLeafRequirementFrom(pathElements, maxRequirementsDepth);
            // Immediate parent requirements
            if (leafPathElements.contains(requirement.getPathElements())) {
                requirementsByPath.put(pathElements, leafLevelRequirements.get(requirement.getPathElements()));
            } else {
                requirementsByPath.put(pathElements, requirement);
            }
            // Add any higher level requirements
            PathElements parentPath = pathElements.getParent();
            while (parentPath != null) {
                if (!parentPath.isEmpty() && !requirementsByPath.containsKey(parentPath) && !leafPathElements.contains(parentPath)) {
                    Requirement nonLeafRequirement = nonLeafRequirementFrom(parentPath, maxRequirementsDepth);
                    // Non-leaf requirements might already have scenarios associated with them, in which case
                    // we don't need to included them here
                    if (leafPathElements.contains(nonLeafRequirement.getPathElements())) {
                        requirementsByPath.put(pathElements, leafLevelRequirements.get(nonLeafRequirement.getPathElements()));
                    } else {
                        requirementsByPath.put(parentPath, nonLeafRequirement);
                    }
                }
                parentPath = parentPath.getParent();
            }
        });

        Collection<Requirement> allRequirements = requirementsByPath.values();
//        new ArrayList<>(leafLevelRequirements.values());
//        allRequirements.addAll(requiremntsByPath.values());

        // Use the map to update the parent requirements
//        nonLeafRequirements.forEach((path, requirement) -> {
//            PathElements parentPath = path.getParent();
//            if (parentPath != null) {
//                Requirement parentRequirement = nonLeafRequirements.get(parentPath);
//                if (parentRequirement != null) {
//                    requirement.setParent(parentRequirement.getPath());
//                }
//            }
//        });

        // Use the map to update the leaf requirements
        allRequirements.forEach(
                requirement -> {
                    PathElements parentPath = requirement.getPathElements().getParent();
                    if (parentPath != null && !parentPath.isEmpty()) {
                        Requirement parentRequirement = requirementsByPath.get(parentPath);
                        if (parentRequirement != null) {
                            requirement.setParent(parentRequirement.getPath());
                        }
                    }
                }
        );

        // Make an alias for any leaf requirements that also appear in the non-leaf requirements.
//        leafLevelRequirements.stream()
//                .filter(requirement -> nonLeafRequirements.containsKey(requirement.getPathElements()))
//                .forEach(
//                        requirement
//                                -> requirementAliases.put(requirement, nonLeafRequirements.get(requirement.getPathElements())
//                        ));
//
//        RequirementCache.getInstance().setRequirementAliases(requirementAliases);
//        // Remove any aliased requirements from the leaf requirements list
//        leafLevelRequirements.removeAll(RequirementCache.getInstance().getRequirementAliases().keySet());
        // Associate child requirements with each non-leaf requirement, including both non-leaf and leaf requirements

        populateChildren(requirementsByPath, allRequirements);

        RequirementCache.getInstance().indexRequirements(requirementsByPath);

        // Return a list of the top-level or leaf requirements with no parent elements
        return allRequirements.stream()
                .filter(requirement -> StringUtils.isEmpty(requirement.getParent()))
                .collect(Collectors.toList());
    }


    /**
     * Update the test outcome with the requirements tags.
     */
    @Override
    public void addRequirementTagsTo(TestOutcome outcome) {
        Map<String, Requirement> index = RequirementCache.getInstance().getRequirementsPathIndex();
        Requirement parentRequirement = index.get(outcome.getPath());
        if (parentRequirement != null) {
            outcome.addTags(requirementTagsFrom(parentRequirement, index));
        }
    }

    private static void populateChildren(Map<PathElements, Requirement> requirementsByPath, Collection<Requirement> allRequirements) {
        requirementsByPath.forEach((path, requirement) -> {
            // Find leaf requirements that are children of this requirement
            List<Requirement> childRequirements = allRequirements.stream()
                    .filter(childRequirement -> childRequirement.hasParent(path))
                    .collect(Collectors.toList());
            // The list of children is now complete
            requirement.setChildren(childRequirements);
        });
    }

    private List<TestTag> requirementTagsFrom(Requirement requirement, Map<String, Requirement> requirementsIndex) {
        List<TestTag> tags = new ArrayList<>();
        if (requirement == null) {
            return tags;
        }

        tags.add(requirement.asTag());
        while (requirement != null && requirement.getParent() != null && !requirement.getParent().isEmpty()) {
            requirement = requirementsIndex.get(requirement.getParent());
            if (requirement != null) {
                tags.add(requirement.asTag());
            }
        }
        return tags;
    }

    private void recordDisplayNameFor(PathElements path) {
        while (path != null) {
            RequirementCache.getInstance().setRequirementDisplayName(path.asPath(), path.getDisplayName());
            path = path.getParent();
        }
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
        String path = relativePath.toString();
        String parentPath = relativePath.getParent().toString();

        String requirementType = requirementsConfiguration.getRequirementType(relativePath.size() - 1, maxRequirementsDepth);

        return Requirement.named(requirementLeaf.getName())
                .withId(StringUtils.isBlank(path) ? requirementLeaf.getName() : path)
                .withType(requirementType)
                .withNarrative("")
                .withDisplayName(requirementLeaf.getDescription())
                .withPath(path)
                .withParent(parentPath);
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

    private Requirement requirementFrom(Story userStory) {//
        PathElement requirementLeaf = userStory.getPathElements().get(userStory.getPathElements().size() - 1);
        String parent = (userStory.getPathElements().getParent() != null) ? userStory.getPathElements().getParent().toString() : "";
        return Requirement.named(requirementLeaf.getName())
                .withId(userStory.getPathElements().toString())
                .withType(userStory.getType())
                .withNarrative(userStory.getNarrative())
                .withDisplayName(requirementLeaf.getDescription())
                .withParent(parent)
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
            aliasedRequirement = RequirementCache.getInstance().getRequirementAliases().keySet().stream()
                    .filter(requirement -> requirement.matchesUserStory(testOutcome.getUserStory()))
                    .map(requirement -> RequirementCache.getInstance().getRequirementAliases().get(requirement))
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
    public Optional<Requirement> getRequirementFor(TestTag testTag) {
        Requirement matchingRequirement
                = RequirementCache.getInstance().getRequirementsByTag(testTag, this::findRequirementByTag);
        return Optional.ofNullable(matchingRequirement);
    }

    private Requirement findRequirementByTag(TestTag tag) {
        for (Requirement requirement : getFlattenedRequirements()) {
            if (requirement.matchesTag(tag)) {
                return requirement;
            }
        }
        return null;
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

    public List<Requirement> getFlattenedRequirements() {
        return getFlattenedRequirements(getRequirements());
    }

    private List<Requirement> flattenedRequirementsFrom(List<Requirement> requirements) {
        List<Requirement> flattenedRequirements = new ArrayList<>();
        for (Requirement requirement : requirements) {
            flattenedRequirements.add(requirement);
            if (requirement.getChildren() != null && !requirement.getChildren().isEmpty()) {
                flattenedRequirements.addAll(flattenedRequirementsFrom(requirement.getChildren()));
            }
        }
        return flattenedRequirements;
    }

    private List<Requirement> getFlattenedRequirements(List<Requirement> requirements) {
        if (RequirementCache.getInstance().getFlattenedRequirements().isEmpty()) {
            RequirementCache.getInstance().updateFlattenedRequirements(flattenedRequirementsFrom(requirements));
        }
        return RequirementCache.getInstance().getFlattenedRequirements();
    }
}
