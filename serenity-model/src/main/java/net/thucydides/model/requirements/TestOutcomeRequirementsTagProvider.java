package net.thucydides.model.requirements;

import com.google.common.base.Splitter;
import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.model.di.ModelInfrastructure;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.*;

import net.thucydides.model.reports.TestOutcomeLoader;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.requirements.model.RequirementsConfiguration;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Read requirements from test outcomes, based on the path specified in each test outcome
 */
public class TestOutcomeRequirementsTagProvider implements RequirementsTagProvider, OverridableTagProvider, RequirementTypesProvider {

    public static final String JUNIT5_FORMAT = "JUnit5";
    public static final String JUNIT4_FORMAT = "JUnit";
    public static final String JAVASCRIPT_FORMAT = "JS";
    public static final String DEFAULT_TARGET_DIR = "target/site/serenity";

    private final RequirementsConfiguration requirementsConfiguration;
    private final EnvironmentVariables environmentVariables;

    private final static List<String> SUPPORTED_TEST_SOURCES = NewList.of(JUNIT5_FORMAT, JUNIT4_FORMAT,JAVASCRIPT_FORMAT);


    private Path sourceDirectory;

    /**
     * Default constructor. Initializes an instance of the TestOutcomeRequirementsTagProvider class with default environment variables.
     */
    public TestOutcomeRequirementsTagProvider() {
        this(ModelInfrastructure.getEnvironmentVariables());
    }

    public TestOutcomeRequirementsTagProvider fromSourceDirectory(Path sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
        return this;
    }

    /**
     * Constructor with EnvironmentVariables parameter. Initializes an instance of the TestOutcomeRequirementsTagProvider class with the provided environment variables.
     *
     * @param environmentVariables The EnvironmentVariables to be used for the TestOutcomeRequirementsTagProvider.
     */
    public TestOutcomeRequirementsTagProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.requirementsConfiguration = new RequirementsConfiguration(environmentVariables);
    }

    /**
     * Get active requirement types based on the depth of each requirement.
     * The requirement types are listed in order of depth.
     *
     * @return List of active requirement types sorted by depth.
     */
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

    /**
     * Get the requirements from the RequirementCache or load them if not present in the cache.
     *
     * @return List of Requirement objects.
     */
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
        File outputDirectory = ConfiguredEnvironment.getConfiguration().getOutputDirectory();

        File requirementsDirectory;
        if ((sourceDirectory != null) && sourceDirectory.toFile().exists()) {
            requirementsDirectory = sourceDirectory.toFile();
        } else {
            requirementsDirectory = outputDirectory;
        }
        // If no output directory exists (yet), the test run is still in progress, so don't bother reading the requirements yet.
        if (requirementsDirectory.exists()) {
            List<TestOutcome> outcomes = loader.loadFrom(requirementsDirectory);

            int maxRequirementsDepth = getMaxRequirementsDepthFrom(outcomes);

            // Bottom-level requirements
            Map<PathElements, Requirement> leafLevelRequirements = getLeafLevelRequirementsFrom(outcomes);

            Set<PathElements> leafPathElements = leafLevelRequirements.keySet();

            Map<PathElements, Requirement> requirementsByPath = new HashMap<>();

            // Non-leaf requirements indexed by path
            findPathElementsIn(outcomes).forEach(pathElements -> processPathElements(pathElements, maxRequirementsDepth, leafPathElements, leafLevelRequirements, requirementsByPath));

            Collection<Requirement> allRequirements = requirementsByPath.values();

            // Use the map to update the leaf requirements
            updateParentFieldsIn(requirementsByPath, allRequirements);

            // Make an alias for any leaf requirements that also appear in the non-leaf requirements.

            populateChildren(requirementsByPath, allRequirements);

            RequirementCache.getInstance().indexRequirements(requirementsByPath);

            // Return a list of the top-level or leaf requirements with no parent elements
            return allRequirements.stream()
                    .filter(requirement -> StringUtils.isEmpty(requirement.getParent()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private void processPathElements(PathElements pathElements, int maxRequirementsDepth, Set<PathElements> leafPathElements,
                                     Map<PathElements, Requirement> leafLevelRequirements, Map<PathElements, Requirement> requirementsByPath) {

        Requirement requirement = nonLeafRequirementFrom(pathElements, maxRequirementsDepth);

        addImmediateParentRequirement(pathElements, requirement, leafPathElements, leafLevelRequirements, requirementsByPath);
        addHigherLevelRequirements(pathElements, maxRequirementsDepth, leafPathElements, leafLevelRequirements, requirementsByPath);
    }

    private void addImmediateParentRequirement(PathElements pathElements, Requirement requirement, Set<PathElements> leafPathElements,
                                               Map<PathElements, Requirement> leafLevelRequirements, Map<PathElements, Requirement> requirementsByPath) {

        if (leafPathElements.contains(requirement.getPathElements())) {
            requirementsByPath.put(pathElements, leafLevelRequirements.get(requirement.getPathElements()));
        } else {
            requirementsByPath.put(pathElements, requirement);
        }
    }

    private void addHigherLevelRequirements(PathElements pathElements, int maxRequirementsDepth, Set<PathElements> leafPathElements,
                                            Map<PathElements, Requirement> leafLevelRequirements, Map<PathElements, Requirement> requirementsByPath) {

        PathElements parentPath = pathElements.getParent();

        while (parentPath != null) {
            if (!parentPath.isEmpty() && !requirementsByPath.containsKey(parentPath) && !leafPathElements.contains(parentPath)) {
                Requirement nonLeafRequirement = nonLeafRequirementFrom(parentPath, maxRequirementsDepth);
                if (leafPathElements.contains(nonLeafRequirement.getPathElements())) {
                    requirementsByPath.put(pathElements, leafLevelRequirements.get(nonLeafRequirement.getPathElements()));
                } else {
                    requirementsByPath.put(parentPath, nonLeafRequirement);
                }
            }
            parentPath = parentPath.getParent();
        }
    }

    private static void updateParentFieldsIn(Map<PathElements, Requirement> requirementsByPath, Collection<Requirement> allRequirements) {
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
    }

    @NotNull
    private List<PathElements> findPathElementsIn(List<TestOutcome> outcomes) {
        return supportedOutcomesFrom(outcomes)
                .map(outcome -> outcome.getUserStory().getPathElements())
                .distinct()
                .map(this::relativePathFrom)
                .filter(pathElements -> !pathElements.isEmpty())
                .collect(Collectors.toList());
    }

    @NotNull
    private Map<PathElements, Requirement> getLeafLevelRequirementsFrom(List<TestOutcome> outcomes) {
        Map<PathElements, Requirement> leafLevelRequirements = supportedOutcomesFrom(outcomes)
                .map(TestOutcome::getUserStory)
                .map(this::requirementFrom)
                .distinct()
                .collect(Collectors.toMap(
                        Requirement::getPathElements,
                        requirement -> requirement
                ));
        return leafLevelRequirements;
    }

    private int getMaxRequirementsDepthFrom(List<TestOutcome> outcomes) {
        int maxRequirementsDepth = supportedOutcomesFrom(outcomes)
                .filter(outcome -> !outcome.getUserStory().getParentPathElements().isEmpty())
                .mapToInt(outcome -> outcome.getUserStory().getParentPathElements().size())// - 1)
                .max()
                .orElse(0);
        return maxRequirementsDepth;
    }


    /**
     * Adds requirement tags to the given TestOutcome.
     * The method gets the parent requirement from the RequirementCache and adds tags from the requirement hierarchy to the TestOutcome.
     *
     * @param outcome The TestOutcome to which the requirement tags are to be added.
     */
    @Override
    public void addRequirementTagsTo(TestOutcome outcome) {
        if (outcome.getPath() == null) {
            return;
        }
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
        return Splitter.on(".").omitEmptyStrings().splitToList(rootPackage);
    }

    private Requirement requirementFrom(Story userStory) {//
        PathElement requirementLeaf = userStory.getPathElements().get(userStory.getPathElements().size() - 1);
        String parent = (userStory.getPathElements().getParent() != null) ? userStory.getPathElements().getParent().toString() : "";
        return Requirement.named(requirementLeaf.getName())
                .withId(userStory.getPathElements().toString())
                .withType(userStory.getType())
                .withNarrative(userStory.getNarrative())
                .withDisplayName(userStory.getDisplayName())
                .withParent(parent)
                .withPath(userStory.getPath());
    }

    /**
     * Fetches the parent requirement of a given TestOutcome.
     * If the TestOutcome has no user story or path, returns an empty Optional.
     * If no requirement matches the user story in the flattened requirements, an aliased requirement is fetched.
     *
     * @param testOutcome The TestOutcome whose parent requirement is to be fetched.
     * @return Optional containing the parent requirement if it exists, else an empty Optional.
     */
    @Override
    public Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome) {
        if ((testOutcome.getUserStory() == null) || (testOutcome.getUserStory().getPath() == null)) {
            return Optional.empty();
        }
        return getFlattenedRequirements().stream()
                .filter(requirement -> requirement.matchesUserStory(testOutcome.getUserStory()))
                .findFirst();
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
