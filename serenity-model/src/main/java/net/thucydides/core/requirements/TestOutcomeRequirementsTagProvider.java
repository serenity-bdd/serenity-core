package net.thucydides.core.requirements;

import com.google.common.base.Splitter;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.*;
import net.thucydides.core.reports.TestOutcomeLoader;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.RequirementsConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static net.thucydides.core.guice.Injectors.getInjector;

/**
 * Read requirements from test outcomes, based on the path specified in each test outcome
 */
public class TestOutcomeRequirementsTagProvider implements RequirementsTagProvider, OverridableTagProvider, RequirementTypesProvider {
    private final RequirementsConfiguration requirementsConfiguration;
    private EnvironmentVariables environmentVariables;

    private List<Requirement> requirements;
    private List<String> requirementTypes;

    public TestOutcomeRequirementsTagProvider() {
        this(getInjector().getProvider(EnvironmentVariables.class).get());
    }

    public TestOutcomeRequirementsTagProvider(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.requirementsConfiguration = new RequirementsConfiguration(environmentVariables);
        this.requirementTypes = requirementsConfiguration.getRequirementTypes();
    }

    @Override
    public List<String> getActiveRequirementTypes() {
        return null;
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

    private List<Requirement> loadRequirements() {
        TestOutcomeLoader loader = new TestOutcomeLoader();
        File outputDirectory = new File(ThucydidesSystemProperty.SERENITY_OUTPUT_DIRECTORY.from(environmentVariables, "target/site/serenity"));
        List<TestOutcome> outcomes = loader.loadFrom(outputDirectory);

        int maxRequirementsDepth = outcomes.stream()
                .filter(outcome -> !outcome.getUserStory().getPathElements().isEmpty())
                .mapToInt(outcome -> outcome.getUserStory().getPathElements().size() - 1)
                .max()
                .orElse(0);

        // Bottom-level requirements
        List<Requirement> leafLevelRequirements = outcomes.stream()
                .map(TestOutcome::getUserStory)
                .distinct()
                .map(this::requirementFrom)
                .collect(Collectors.toList());

        // Non-leaf requirements indexed by path
        Map<PathElements, Requirement> nonLeafRequirements = outcomes.stream()
                .map(outcome -> outcome.getUserStory().getPathElements())
                .filter(pathElements -> !pathElements.isEmpty())
                .distinct()
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
                    PathElements parentPath = requirement.getPathElements(environmentVariables);
                    if (parentPath != null && !parentPath.isEmpty()) {
                        Requirement parentRequirement = nonLeafRequirements.get(parentPath);
                        if (parentRequirement != null) {
                            requirement.setParent(parentRequirement.getPath());
                        }
                    }
                }
        );

        List<Requirement> parentRequirements;

        return null;
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
    private Requirement nonLeafRequirementFrom(List<PathElement> pathElements, int maxRequirementsDepth) {

        List<PathElement> relativePath = relativePathFrom(pathElements);
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

    private List<PathElement> relativePathFrom(List<PathElement> pathElements) {
        String rootPackage = ThucydidesSystemProperty.SERENITY_TEST_ROOT.from(environmentVariables);
        List<String> rootPackageElements = Splitter.on(".").splitToList(rootPackage);
        List<PathElement> relativePathElements = new ArrayList<>(pathElements);
        for (String rootPackageElement : rootPackageElements) {
            if (relativePathElements.get(0).getName().equals(rootPackageElement)) {
                relativePathElements.remove(0);
            }
        }
        return relativePathElements;
    }

    private Requirement requirementFrom(Story userStory) {
        return Requirement.named(userStory.getName()).withType(userStory.getType())
                .withNarrative(userStory.getNarrative())
                .withPath(userStory.getPath());
    }

    @Override
    public Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome) {
        return Optional.empty();
    }

    @Override
    public Optional<Requirement> getParentRequirementOf(Requirement requirement) {
        return Optional.empty();
    }

    @Override
    public Optional<Requirement> getRequirementFor(TestTag testTag) {
        return Optional.empty();
    }

    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        return null;
    }
}
