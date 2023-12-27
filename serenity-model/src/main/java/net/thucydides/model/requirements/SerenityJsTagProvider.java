package net.thucydides.model.requirements;

import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.domain.RequirementCache;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.reports.TestOutcomeLoader;
import net.thucydides.model.requirements.model.Requirement;
import net.thucydides.model.util.EnvironmentVariables;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SerenityJsTagProvider implements RequirementsTagProvider, OverridableTagProvider, RequirementTypesProvider {

    private static final List<Requirement> NO_REQUIREMENTS = new ArrayList<>();
    public static final String SERENITY_JS_FORMAT = "Serenity/JS";
    private final static List<String> SUPPORTED_TEST_SOURCES = NewList.of(SERENITY_JS_FORMAT);

    private final RequirementCache requirementCache;
    private final TestOutcomeLoader testOutcomeLoader;
    private final EnvironmentVariables environmentVariables;
    private final Path testScenariosDirectory;
    private final Path jsonOutcomesDirectory;
    private final Path outputDirectory;

    private volatile List<Requirement> requirements;

    public SerenityJsTagProvider(
            RequirementCache requirementCache,
            TestOutcomeLoader testOutcomeLoader,
            EnvironmentVariables environmentVariables,
            Path testScenariosDirectory,
            Path jsonOutcomesDirectory,
            Path outputDirectory
    ) {
        this.requirementCache = requirementCache;
        this.testOutcomeLoader = testOutcomeLoader;
        this.environmentVariables = environmentVariables;
        this.testScenariosDirectory = testScenariosDirectory;
        this.jsonOutcomesDirectory = jsonOutcomesDirectory;
        this.outputDirectory = outputDirectory;
    }

    @Override
    public List<String> getActiveRequirementTypes() {
        return List.of();
    }

    @Override
    public List<Requirement> getRequirements() {
        if (requirements == null) { // double-checked locking
            synchronized (this) {
                if (requirements == null) {

                    List<TestOutcome> outcomes = testOutcomeLoader.loadFrom(jsonOutcomesDirectory.toFile());

                    int maxRequirementsDepth = getMaxRequirementsDepthFrom(outcomes);



                    
                    List<Requirement> loadedRequirements = rootDirectoriesFor(testScenariosDirectory)
                            .flatMap(this::discoverRequirements)
                            .sorted()
                            .collect(Collectors.toList());
//                    if (addParents) {
//                        RequirementAncestry.addParentsTo(loadedRequirements);
//                    }
                    this.requirements = loadedRequirements;
//                    requirementCache.indexRequirements(indexByPath(requirements));
                }
            }
        }

        return requirements;
    }

    private int getMaxRequirementsDepthFrom(List<TestOutcome> outcomes) {
        int maxRequirementsDepth = supportedOutcomesFrom(outcomes)
                .filter(outcome -> !outcome.getUserStory().getParentPathElements().isEmpty())
                .mapToInt(outcome -> outcome.getUserStory().getParentPathElements().size())// - 1)
                .max()
                .orElse(0);
        return maxRequirementsDepth;
    }

    private Stream<TestOutcome> supportedOutcomesFrom(List<TestOutcome> outcomes) {
        return outcomes.stream()
                .filter(outcome -> (outcome.getTestSource() == null) || SUPPORTED_TEST_SOURCES.contains(outcome.getTestSource()));
    }

    private Stream<String> rootDirectoriesFor(Path directory) {
        return new RootDirectory(environmentVariables, directory.toString())
                .getRootDirectoryPaths()
                .stream();
    }

    private Stream<Requirement> discoverRequirements(String path) {
        File rootDirectory = new File(path);

//        if (! (rootDirectory.exists() && rootDirectory.isDirectory())) {
            return NO_REQUIREMENTS.stream();
//        }

//        return Stream.concat(
//                loadRequirementsFrom(rootDirectory.listFiles(thatAreFeatureOrSpecDirectories())),
//                loadStoriesFrom(rootDirectory.listFiles(thatAreStories()))
//        );
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
        return Set.of();
    }
}
