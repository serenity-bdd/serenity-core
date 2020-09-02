package net.thucydides.core.requirements;

import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.files.TheDirectoryStructure;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.*;
import net.thucydides.core.requirements.model.cucumber.CucumberParser;
import net.thucydides.core.requirements.model.cucumber.InvalidFeatureFileException;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.thucydides.core.files.TheDirectoryStructure.startingAt;
import static net.thucydides.core.requirements.RequirementsPath.pathElements;
import static net.thucydides.core.requirements.RequirementsPath.stripRootFromPath;
import static net.thucydides.core.util.Inflector.inflection;
import static net.thucydides.core.util.NameConverter.humanize;
import static org.apache.commons.io.FilenameUtils.removeExtension;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Load a set of requirements (epics/themes,...) from the directory structure.
 * This will typically be the directory structure containing the tests (for JUnit), stories (JBehave) or features (Cucumber).
 */
public class FileSystemRequirementsTagProvider extends AbstractRequirementsTagProvider implements RequirementsTagProvider, OverridableTagProvider {

    private final static Logger logger = LoggerFactory.getLogger(FileSystemRequirementsTagProvider.class);

    private static final List<Requirement> NO_REQUIREMENTS = new ArrayList<>();
    private static final List<TestTag> NO_TEST_TAGS = new ArrayList<>();
    private static final String STORY_EXTENSION = "story";
    private static final String FEATURE_EXTENSION = "feature";

    private final NarrativeReader narrativeReader;
    private final OverviewReader overviewReader;
    private final Set<String> directoryPaths;
    private final int level;
    private final List<String> requirementTypes;

    private final static String DEFAULT_FEATURE_DIRECTORY = "src/test/resources/features";

    private final RequirementsConfiguration requirementsConfiguration;

    //    @Transient
    private volatile List<Requirement> requirements;

    public FileSystemRequirementsTagProvider(EnvironmentVariables environmentVariables) {
        this(environmentVariables,
                RootDirectory.definedIn(environmentVariables).featuresOrStoriesRootDirectory().orElse(Paths.get(DEFAULT_FEATURE_DIRECTORY)).toString());
    }

    public FileSystemRequirementsTagProvider(EnvironmentVariables environmentVariables, String rootDirectoryPath) {
        this(rootDirectoryPath, environmentVariables);
    }

    public FileSystemRequirementsTagProvider() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get());
    }

    public FileSystemRequirementsTagProvider(String rootDirectory, int level) {
        this(filePathFormOf(rootDirectory), level, Injectors.getInjector().getProvider(EnvironmentVariables.class).get());
    }

    private String baseDirectory() {
        if (level == 0) {
            return rootDirectory;
        }
        Path baseDirectory = Paths.get(rootDirectory);
        for (int step = 0; step < level; step++) {
            if (baseDirectory.getParent() != null) {
                baseDirectory = baseDirectory.getParent();
            }
        }
        return baseDirectory.toString();
    }

    /**
     * Convert a package name to a file path if necessary.
     */
    private static String filePathFormOf(String rootDirectory) {
        if (rootDirectory.contains(".")) {
            return rootDirectory.replace(".", "/");
        } else {
            return rootDirectory;
        }
    }

    private int maxDepth;
    private final String topLevelDirectory;
    private Set<String> requirementsDirectoryPaths;

    public FileSystemRequirementsTagProvider(String rootDirectory, EnvironmentVariables environmentVariables) {
        super(environmentVariables, rootDirectory);

        topLevelDirectory = rootDirectory;
        requirementTypes = new RequirementsConfiguration(environmentVariables, rootDirectory).getRequirementTypes();
        this.narrativeReader = NarrativeReader.forRootDirectory(environmentVariables, rootDirectory);
//                .withRequirementTypes(getRequirementTypes(rootDirectory));
        this.overviewReader = new OverviewReader();
        this.requirementsConfiguration = new RequirementsConfiguration(environmentVariables);

        directoryPaths = rootDirectories(rootDirectory, environmentVariables);

        this.level = requirementsConfiguration.startLevelForADepthOf(maxDirectoryDepthIn(directoryPaths) + 1);
        maxDepth = maxDirectoryDepthIn(directoryPaths);
        requirementsDirectoryPaths = RootDirectory.definedIn(environmentVariables).requirementsDirectoryNames();
    }

    public FileSystemRequirementsTagProvider(String rootDirectory, int level, EnvironmentVariables environmentVariables) {
        this(rootDirectory, rootDirectory, level, environmentVariables);
    }

    public FileSystemRequirementsTagProvider(String topLevelDirectory, String rootDirectory, int level, EnvironmentVariables environmentVariables) {
        super(environmentVariables, rootDirectory);

        this.topLevelDirectory = topLevelDirectory;
        requirementTypes = new RequirementsConfiguration(environmentVariables, rootDirectory).getRequirementTypes();
        this.narrativeReader = NarrativeReader.forRootDirectory(environmentVariables, rootDirectory);
//                .withRequirementTypes(getRequirementTypes());
        this.overviewReader = new OverviewReader();
        directoryPaths = rootDirectories(rootDirectory, environmentVariables);
        this.requirementsConfiguration = new RequirementsConfiguration(environmentVariables);
        this.level = level;//requirementsConfiguration.initialLevel();
        maxDepth = maxDirectoryDepthIn(directoryPaths);
    }

    private static Set<String> rootDirectories(String rootDirectory, EnvironmentVariables environmentVariables) {
        return new RootDirectory(environmentVariables, rootDirectory).getRootDirectoryPaths();
    }

    public FileSystemRequirementsTagProvider(String rootDirectory) {
        this(filePathFormOf(rootDirectory), Injectors.getInjector().getProvider(EnvironmentVariables.class).get());
    }

    private final Object requirementsLock = new Object();

    /**
     * We look for file system requirements in the root directory path (by default, 'stories').
     * First, we look on the classpath. If we don't find anything on the classpath (e.g. if the task is
     * being run from the Maven plugin), we look in the src/main/resources and src/test/resources directories starting
     * at the working directory.
     */
    public List<Requirement> getRequirements() {

        if (requirements == null) {
            synchronized (requirementsLock) {
                if (requirements == null) {

                    requirements = getRootDirectoryPaths()
                            .stream()
                            .map(this::capabilitiesAndStoriesIn)
                            .flatMap(Collection::stream)
                            .sorted()
                            .collect(Collectors.toList());

                    requirements = RequirementAncestry.addParentsTo(requirements);
                }
            }
        }
        return requirements;
    }

    private Set<Requirement> capabilitiesAndStoriesIn(String path) {
        Set<Requirement> allRequirements = new HashSet<>();
        File rootDirectory = new File(path);
        if (rootDirectory.exists()) {

            loadCapabilitiesFrom(rootDirectory.listFiles(thatAreFeatureDirectories())).forEach(
                    capability -> allRequirements.add(capability)
            );

            loadStoriesFrom(rootDirectory.listFiles(thatAreStories())).forEach(
                    leafRequirement -> allRequirements.add(leafRequirement)
            );
        }
        return allRequirements;
    }

    private int maxDirectoryDepthIn(Set<String> directoryPaths) {
        return directoryPaths.stream()
                .mapToInt(directoryPath -> TheDirectoryStructure.startingAt(new File(directoryPath)).maxDepth())
                .max().orElse(0);
    }

    /**
     * Find the root directory in the classpath or on the file system from which the requirements will be read.
     */
    public Set<String> getRootDirectoryPaths() {
        return new RootDirectory(environmentVariables, rootDirectory).getRootDirectoryPaths();
    }

    public Set<TestTag> getTagsFor(final TestOutcome testOutcome) {
        //
        // For the FileSystemRequirements tag provider, the test outcome provides a path, which might be:
        //   - a feature file for Cucumber ("add_an_item.feature"),
        //   - a story file with a path (JBehave) ("stories/manage_items/add_an_item.story")
        //
        Set<TestTag> tags = new HashSet<>();
        if (testOutcome.getPath() != null) {

            java.util.Optional<Requirement> matchingRequirement = requirementWithMatchingFeatureFile(testOutcome);

            matchingRequirement.ifPresent(
                    requirement -> {
                        tags.add(requirement.asTag());
                        tags.addAll(parentRequirementsOf(requirement.asTag()));
                    }
            );

            // Strategy used if a full path is provided
            List<String> storyPathElements = stripRootFrom(pathElements(stripRootPathFrom(testOutcome.getPath())));
            tags.addAll(getMatchingCapabilities(getRequirements(), stripStorySuffixFrom(storyPathElements)));

            if (tags.isEmpty() && storyOrFeatureDescribedIn(storyPathElements).isPresent()) {
                java.util.Optional<TestTag> matchingRequirementTag = getMatchingRequirementTagsFor(storyOrFeatureDescribedIn(storyPathElements).get());
                if (matchingRequirementTag.isPresent()) {
                    tags.add(matchingRequirementTag.get());
                    tags.addAll(parentRequirementsOf(matchingRequirementTag.get()));
                }
            }
        }
        return tags;
    }

    java.util.Optional<Requirement> requirementWithMatchingFeatureFile(TestOutcome testOutcome) {
        String candidatePath = testOutcome.getPath();
        String parentRequirementId = testOutcome.getParentId();

        return AllRequirements.asStreamFrom(getRequirements())
                .filter(
                        requirement ->
                                (requirement.getId() != null && requirement.getId().equals(parentRequirementId)) ||
                                        ((requirement.getFeatureFileName() != null) && (requirement.getFeatureFileName().equalsIgnoreCase(candidatePath))) ||
                                        ((requirement.getPath() != null) && (equivalentPaths(requirement.getPath(), candidatePath)))
                ).findFirst();
    }


    private Collection<TestTag> parentRequirementsOf(TestTag requirementTag) {
        List<TestTag> matchingTags = new ArrayList<>();

        java.util.Optional<Requirement> matchingRequirement = getMatchingRequirementFor(requirementTag);
        java.util.Optional<Requirement> parent = parentRequirementsOf(matchingRequirement.get());
        while (parent.isPresent()) {
            matchingTags.add(parent.get().asTag());
            parent = parentRequirementsOf(parent.get());
        }
        return matchingTags;
    }

    private java.util.Optional<Requirement> parentRequirementsOf(Requirement matchingRequirement) {

        return AllRequirements.asStreamFrom(getRequirements())
                .filter(requirement -> requirement.getChildren().contains(matchingRequirement))
                .findFirst();
    }

    private List<String> stripStorySuffixFrom(List<String> pathElements) {
        if ((!pathElements.isEmpty()) && isSupportedFileStoryExtension(last(pathElements))) {
            return dropLastElement(pathElements);
        } else {
            return pathElements;
        }
    }

    private List<String> dropLastElement(List<String> pathElements) {
        List<String> strippedPathElements = new ArrayList<>(pathElements);
        strippedPathElements.remove(pathElements.size() - 1);
        return strippedPathElements;
    }

    private java.util.Optional<Requirement> getMatchingRequirementFor(TestTag storyOrFeatureTag) {

        return AllRequirements.asStreamFrom(getRequirements())
                .filter(
                        requirement -> requirement.asTag().isAsOrMoreSpecificThan(storyOrFeatureTag)
                )
                .findFirst();
    }

    private java.util.Optional<TestTag> getMatchingRequirementTagsFor(TestTag storyOrFeatureTag) {
        java.util.Optional<Requirement> matchingRequirement = getMatchingRequirementFor(storyOrFeatureTag);
        return matchingRequirement.map(Requirement::asTag);
    }

    private java.util.Optional<TestTag> storyOrFeatureDescribedIn(List<String> storyPathElements) {
        if ((!storyPathElements.isEmpty()) && isSupportedFileStoryExtension(last(storyPathElements))) {
            String storyName = NewList.reverse(storyPathElements).get(1); // TODO: Get the story or feature name from the file only as a last resort
            String storyParent = parentElement(storyPathElements);
            String qualifiedName = storyParent == null ?
                    humanize(storyName) : humanize(storyParent).trim() + "/" + humanize(storyName);
            TestTag storyTag = TestTag.withName(qualifiedName).andType((last(storyPathElements)));
            return java.util.Optional.of(storyTag);
        } else {
            return java.util.Optional.empty();
        }
    }


    private String parentElement(List<String> storyPathElements) {
        return storyPathElements.size() > 2 ? NewList.reverse(storyPathElements).get(2) : null;
    }

    private String last(List<String> list) {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }

    public java.util.Optional<Requirement> getParentRequirementOf(final TestOutcome testOutcome) {
        return firstRequirementFoundIn(
                parentRequirementFromPackagePath(testOutcome),
                requirementWithMatchingParentId(testOutcome),
                requirementWithMatchingPath(testOutcome),
                featureTagRequirementIn(testOutcome),
                mostSpecificTagRequirementFor(testOutcome));
    }

    private java.util.Optional<Requirement> featureTagRequirementIn(TestOutcome testOutcome) {
        List<String> storyPathElements = stripStorySuffixFrom(stripRootFrom(pathElements(stripRootPathFrom(testOutcome.getPath()))));
        return lastRequirementFrom(storyPathElements);
    }

    private java.util.Optional<Requirement> parentRequirementFromPackagePath(TestOutcome testOutcome) {
        if (testOutcome.getPath() != null) {
            List<String> storyPathElements = stripStorySuffixFrom(stripRootFrom(pathElements(stripRootPathFrom(testOutcome.getPath()))));
            return lastRequirementFrom(storyPathElements);
        }
        return Optional.empty();
    }

    private java.util.Optional<Requirement> mostSpecificTagRequirementFor(TestOutcome testOutcome) {
        java.util.Optional<Requirement> mostSpecificRequirement = Optional.empty();
        int currentSpecificity = -1;

        for (TestTag tag : testOutcome.getTags()) {
            java.util.Optional<Requirement> matchingRequirement = getRequirementFor(tag);
            if (matchingRequirement.isPresent()) {
                int specificity = requirementsConfiguration.getRequirementTypes().indexOf(matchingRequirement.get().getType());
                if (currentSpecificity < specificity) {
                    currentSpecificity = specificity;
                    mostSpecificRequirement = matchingRequirement;
                }
            }
        }
        return mostSpecificRequirement;
    }

    private java.util.Optional<Requirement> requirementWithMatchingPath(TestOutcome testOutcome) {

        Path testOutcomeRequirementsPath =  RootDirectory.definedIn(environmentVariables).getRelativePathOf(testOutcome.getPath());

        Optional<Requirement> requirementWithMatchingPath = AllRequirements.asStreamFrom(getRequirements())
                .filter(requirement -> requirementHasPathMatching(requirement, testOutcomeRequirementsPath)).findFirst();

        Optional<Requirement> requirementWithAMatchingName = AllRequirements.asStreamFrom(getRequirements())
                .filter(requirement -> requirementHasNameMatching(requirement, testOutcomeRequirementsPath)).findFirst();


        if (requirementWithMatchingPath.isPresent()) {
            return requirementWithMatchingPath;
        }
        if (requirementWithAMatchingName.isPresent()) {
            return requirementWithAMatchingName;
        }

        if (testOutcome.getPath() == null) {
            return Optional.empty();
        }

        return AllRequirements.asStreamFrom(getRequirements())
                .filter(requirement -> requirement.getPath() != null)
                .filter(requirement -> equivalentPaths(requirement.getPath(), testOutcome.getPath()))
                .findFirst();
    }

    private boolean requirementHasPathMatching(Requirement requirement, Path expectedPath) {
        return requirement.getPath() != null  && expectedPath.equals(Paths.get(requirement.getPath()));
    }

    private boolean requirementHasNameMatching(Requirement requirement,Path expectedPath) {
        return requirement.getFeatureFileName() != null  && expectedPath.equals(Paths.get(requirement.getFeatureFileName()));
    }


    private boolean equivalentPaths(String pathA, String pathB) {
        String normalisedPathA = removeFeatureOrStoryPrefixFrom(pathA.replaceAll("[/\\\\]", "/")).replaceAll("\\.", "/").replaceAll(" ", "_");
        String normalisedPathB = removeFeatureOrStoryPrefixFrom(pathB.replaceAll("[/\\\\]", "/")).replaceAll("\\.", "/").replaceAll(" ", "_");
        return normalisedPathA.equalsIgnoreCase(normalisedPathB);
    }

    private String removeFeatureOrStoryPrefixFrom(String path) {
        String stories = RootDirectory.definedIn(environmentVariables).storyDirectoryName();
        String features = RootDirectory.definedIn(environmentVariables).featureDirectoryName();
        if (path.startsWith(stories)) {
            path = path.substring(stories.length() + 1);
        }
        if (path.startsWith(features)) {
            path = path.substring(stories.length() + 1);
        }
        if (path.endsWith(".story")) {
            path = path.substring(0, path.length() - 6);
        }
        if (path.endsWith(".feature")) {
            path = path.substring(0, path.length() - 8);
        }
        return path;
    }

    private java.util.Optional<Requirement> requirementWithMatchingParentId(TestOutcome testOutcome) {

        return AllRequirements.asStreamFrom(getRequirements())
                .filter(
                        requirement -> (requirement.getId() != null
                                && testOutcome.getParentId() != null
                                && (requirement.getId().equals(testOutcome.getParentId())))
                )
                .findFirst();
    }

    public java.util.Optional<Requirement> getRequirementFor(TestTag testTag) {
        return AllRequirements.asStreamFrom(getRequirements())
                .filter(
                        requirement -> (requirement.getName().equalsIgnoreCase(testTag.getName())
                                && requirement.getType().equalsIgnoreCase(testTag.getType()))
                )
                .findFirst();
    }

    private java.util.Optional<Requirement> lastRequirementFrom(List<String> storyPathElements) {
        if (storyPathElements.isEmpty()) {
            return Optional.empty();
        } else {
            return lastRequirementMatchingPath(getRequirements(), storyPathElements);
        }
    }

    private java.util.Optional<Requirement> lastRequirementMatchingPath(List<Requirement> requirements, List<String> storyPathElements) {
        if (storyPathElements.isEmpty()) {
            return Optional.empty();
        }
        java.util.Optional<Requirement> matchingRequirement = findMatchingRequirementIn(next(storyPathElements), requirements);
        if (!matchingRequirement.isPresent()) {
            return Optional.empty();
        }
        if (tail(storyPathElements).isEmpty()) {
            return matchingRequirement;
        }
        List<Requirement> childRequrements = matchingRequirement.get().getChildren();
        return lastRequirementMatchingPath(childRequrements, tail(storyPathElements));
    }

    private List<TestTag> getMatchingCapabilities(List<Requirement> requirements, List<String> storyPathElements) {
        if (storyPathElements.isEmpty()) {
            return NO_TEST_TAGS;
        } else {
            java.util.Optional<Requirement> matchingRequirement = findMatchingRequirementIn(next(storyPathElements), requirements);
            if (matchingRequirement.isPresent()) {
                TestTag thisTag = matchingRequirement.get().asTag();
                List<TestTag> remainingTags = getMatchingCapabilities(matchingRequirement.get().getChildren(), tail(storyPathElements));
                return concat(thisTag, remainingTags);
            } else {
                return NO_TEST_TAGS;
            }
        }
    }

    private List<String> stripRootFrom(List<String> storyPathElements) {
        return stripRootFromPath(rootDirectory, storyPathElements);
    }

    private String stripRootPathFrom(String testOutcomePath) {
        if (testOutcomePath == null) {
            return "";
        }
        String rootPath = ThucydidesSystemProperty.SERENITY_TEST_ROOT.from(environmentVariables);
        if (StringUtils.isNotEmpty(rootPath) && testOutcomePath.startsWith(rootPath) && (!testOutcomePath.equals(rootPath))) {
            return testOutcomePath.substring(rootPath.length() + 1);
        } else {
            return testOutcomePath;
        }
    }

    private List<TestTag> concat(TestTag thisTag, List<TestTag> remainingTags) {
        List<TestTag> totalTags = new ArrayList<>();
        totalTags.add(thisTag);
        totalTags.addAll(remainingTags);
        return totalTags;
    }

    private <T> T next(List<T> elements) {
        return elements.get(0);
    }

    private <T> List<T> tail(List<T> elements) {
        return elements.subList(1, elements.size());
    }

    private java.util.Optional<Requirement> findMatchingRequirementIn(String storyPathElement, List<Requirement> requirements) {
        for (Requirement requirement : requirements) {
            String normalizedStoryPathElement = Inflector.getInstance().humanize(Inflector.getInstance().underscore(storyPathElement));
            if (requirement.getName().equals(normalizedStoryPathElement)
                    || (storyPathElement.equalsIgnoreCase(removeExtension(requirement.getFeatureFileName())))
                    || (storyPathElement.equalsIgnoreCase(requirement.getName()))) {
                return Optional.of(requirement);
            }
        }
        return Optional.empty();
    }

    private Stream<Requirement> loadCapabilitiesFrom(File[] requirementDirectories) {

        return Arrays.stream(requirementDirectories)
                .map(this::readRequirementFrom);
    }


    private Stream<Requirement> loadStoriesFrom(File[] storyFiles) {
        return Arrays.stream(storyFiles)
                .map(this::readRequirementsFromStoryOrFeatureFile)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public Requirement readRequirementFrom(File requirementDirectory) {
        java.util.Optional<Narrative> requirementNarrative = narrativeReader.loadFrom(requirementDirectory, Math.max(0, level - 1));

        if (requirementNarrative.isPresent()) {
            return requirementWithNarrative(requirementDirectory,
                    humanReadableVersionOf(requirementDirectory.getName()),
                    requirementNarrative.get());
        } else {
            return requirementFromDirectoryName(requirementDirectory);
        }
    }

    private Set<File> invalidFeatureFiles = new HashSet<>();

    public Optional<Requirement> readRequirementsFromStoryOrFeatureFile(File storyFile) {

        if (invalidFeatureFiles.contains(storyFile)) { return Optional.empty(); }

        FeatureType type = featureTypeOf(storyFile);

        try {
            java.util.Optional<Narrative> narrative = (type == FeatureType.STORY) ? loadFromStoryFile(storyFile) : loadFromFeatureFile(storyFile);

            String storyName = storyNameFrom(narrative, type, storyFile);

            Requirement requirement;
            if (narrative.isPresent()) {
                requirement = leafRequirementWithNarrative(storyName,
                        storyFile.getPath(),
                        narrative.get()).withType(type.toString());

                if (narrative.get().getScenarios().isEmpty()) {
                    requirement = requirement.withNoScenarios();
                }
            } else {
                requirement = storyNamed(storyName, storyFile.getPath()).withType(type.toString());
            }

            return Optional.of(requirement.definedInFile(storyFile));
        } catch (InvalidFeatureFileException invalidFeatureFile) {
            invalidFeatureFiles.add(storyFile);
            return Optional.empty();
        }
    }

    private String storyNameFrom(java.util.Optional<Narrative> narrative, FeatureType type, File storyFile) {
        if (narrative.isPresent() && isNotBlank(narrative.get().getTitle().orElse(""))) {
            return narrative.get().getTitle().get();
        } else {
            if (isSnakeCase(storyFile.getName())) {
                return storyFile.getName().replace(type.getExtension(), "").replace("_", " ");
            } else {
                String storyNameWithoutExtension = storyFile.getName().replace(type.getExtension(), "");
                String snakeCaseStoryName = inflection().underscore(storyNameWithoutExtension);
                return inflection().of(snakeCaseStoryName).asATitle().toString();
            }
        }

    }

    private boolean isSnakeCase(String name) {
        return name.contains("_");
    }

    private java.util.Optional<Narrative> loadFromStoryFile(File storyFile) {
        return narrativeReader.loadFromStoryFile(storyFile);
    }

    private java.util.Optional<Narrative> loadFromFeatureFile(File storyFile) {
        String explicitLocale = readLocaleFromFeatureFile(storyFile);
        CucumberParser parser = (explicitLocale != null) ?
                new CucumberParser(explicitLocale, environmentVariables) : new CucumberParser(environmentVariables);
        return parser.loadFeatureNarrative(storyFile);
    }

    private String readLocaleFromFeatureFile(File storyFile) {
        try {
            List<String> featureFileLines = FileUtils.readLines(storyFile, Charset.defaultCharset());
            for (String line : featureFileLines) {
                if (line.startsWith("#") && line.contains("language:")) {
                    return line.substring(line.indexOf("language:") + 10).trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private FeatureType featureTypeOf(File storyFile) {
        return (storyFile.getName().endsWith("." + STORY_EXTENSION)) ? FeatureType.STORY : FeatureType.FEATURE;
    }

    private Requirement requirementFromDirectoryName(File requirementDirectory) {
        String requirementType = getRequirementTypeOf(requirementDirectory);
        String shortName = humanReadableVersionOf(requirementDirectory.getName());
        List<Requirement> children = readChildrenFrom(requirementDirectory);
        return Requirement.named(shortName)
                .withType(requirementType)
                .withNarrative("")
                .withPath(relativeDirectoryOf(requirementDirectory.getPath()))
                .withChildren(children);
    }

    private String getRequirementTypeOf(File requirementDirectory) {
        int depth = requirementDepthOf(topLevelDirectory, requirementDirectory);
        int maxDepth = TheDirectoryStructure.startingAt(directoryAt(topLevelDirectory)).maxDepth();
        return getDefaultType(depth, maxDepth);
    }

    private File directoryAt(String path) {

        try {
            if (getClass().getClassLoader().getResource(path) != null) {
                return new File(getClass().getClassLoader().getResource(path).toURI());
            }
            return new File(path);
        } catch (URISyntaxException invalidRootDirectory) {
            throw new SerenityManagedException(invalidRootDirectory);
        }
    }

    private int requirementDepthOf(String rootDirectory, File requirementDirectory) {
        if (rootDirectory.equals(requirementDirectory.getPath())) {
            return 0;
        }

        String relativePath = requirementDirectory.getPath().substring(requirementDirectory.getPath().indexOf(rootDirectory) + rootDirectory.length() + 1);
        return relativePath.split("\\/|\\\\").length - 1;
    }

    private String relativeDirectoryOf(String path) {

        String baseDirectory = baseDirectory();
        if (path.contains(baseDirectory)) {
            int relativePathStartsAt = path.indexOf(baseDirectory) + baseDirectory.length() + 1;
            return (relativePathStartsAt < path.length()) ? path.substring(relativePathStartsAt) : "";
        } else {
            return path;
        }
    }

    private Requirement storyNamed(String storyName, String path) {
        String shortName = humanReadableVersionOf(storyName);
        return Requirement.named(shortName)
                .withType(STORY_EXTENSION)
                .withNarrative(shortName)
                .withPath(relativeDirectoryOf(path));
    }

    private Requirement leafRequirementWithNarrative(String shortName, String path, Narrative requirementNarrative) {
        String displayName = getTitleFromNarrativeOrDirectoryName(requirementNarrative, shortName);
        String cardNumber = requirementNarrative.getCardNumber().orElse(null);
        String type = requirementNarrative.getType();
        List<String> releaseVersions = requirementNarrative.getVersionNumbers();
        return Requirement.named(shortName)
                .withId(requirementNarrative.getId().orElse(path))
                .withOptionalDisplayName(displayName)
                .withOptionalCardNumber(cardNumber)
                .withType(type)
                .withNarrative(requirementNarrative.getText())
                .withPath(relativeDirectoryOf(path))
                .withReleaseVersions(releaseVersions)
                .withTags(requirementNarrative.getTags())
                .withScenarioTags(requirementNarrative.getScenarioTags());
    }

    private Requirement requirementWithNarrative(File requirementDirectory, String shortName, Narrative requirementNarrative) {
        String displayName = getTitleFromNarrativeOrDirectoryName(requirementNarrative, shortName);
        String cardNumber = requirementNarrative.getCardNumber().orElse(null);
        String type = requirementNarrative.getType();
        List<String> releaseVersions = requirementNarrative.getVersionNumbers();
        List<Requirement> children = readChildrenFrom(requirementDirectory);
        return Requirement.named(shortName)
                .withOptionalDisplayName(displayName)
                .withOptionalCardNumber(cardNumber)
                .withType(type)
                .withNarrative(requirementNarrative.getText())
                .withReleaseVersions(releaseVersions)
                .withPath(relativeDirectoryOf(requirementDirectory.getPath()))
                .withChildren(children);
    }

    private List<Requirement> readChildrenFrom(File requirementDirectory) {
        String childDirectory = rootDirectory + "/" + requirementDirectory.getName();
        if (childrenExistFor(childDirectory)) {
            RequirementsTagProvider childReader = new FileSystemRequirementsTagProvider(rootDirectory, childDirectory, level + 1, environmentVariables);
            return childReader.getRequirements();
        } else if (childrenExistFor(requirementDirectory.getPath())) {
            RequirementsTagProvider childReader = new FileSystemRequirementsTagProvider(rootDirectory, requirementDirectory.getPath(), level + 1, environmentVariables);
            return childReader.getRequirements();
        } else {
            return NO_REQUIREMENTS;
        }
    }

    private boolean childrenExistFor(String path) {
        if (hasSubdirectories(path)) {
            return true;
        } else if (hasFeatureOrStoryFiles(path)) {
            return true;
        } else {
            return classpathResourceExistsFor(path);
        }
    }

    private boolean hasFeatureOrStoryFiles(String path) {
        File requirementDirectory = new File(path);
        if (requirementDirectory.isDirectory()) {
            return ((requirementDirectory.list(storyFiles()).length > 0) || (requirementDirectory.list(featureFiles()).length > 0));
        } else {
            return false;
        }
    }

    private FilenameFilter storyFiles() {
        return (dir, name) -> name.endsWith(".story");
    }

    private FilenameFilter featureFiles() {
        return (dir, name) -> name.endsWith(".feature");
    }

    private boolean classpathResourceExistsFor(String path) {
        return getClass().getResource(resourcePathFor(path)) != null;
    }

    private String resourcePathFor(String path) {
        return (path.startsWith("/")) ? path : "/" + path;
    }

    private boolean hasSubdirectories(String path) {
        File pathDirectory = new File(path);
        if (!pathDirectory.exists()) {
            return false;
        }
        for (File subdirectory : pathDirectory.listFiles()) {
            if (subdirectory.isDirectory()) {
                return true;
            }
        }
        return false;
    }

    private String getTitleFromNarrativeOrDirectoryName(Narrative requirementNarrative, String nameIfNoNarrativePresent) {
        if (requirementNarrative.getTitle().isPresent() && isNotBlank(requirementNarrative.getTitle().get())) {
            return requirementNarrative.getTitle().get();
        }
        return nameIfNoNarrativePresent;
    }

    private FileFilter thatAreFeatureDirectories() {
        return file -> !file.getName().startsWith(".") && storyOrFeatureFilesExistIn(file);
    }

    private boolean storyOrFeatureFilesExistIn(File directory) {
        return startingAt(directory).containsFiles(thatAreStories(), thatAreNarratives());
    }

    private FileFilter thatAreStories() {
        return file -> {
            String filename = file.getName().toLowerCase();
            if (filename.startsWith("given") || filename.startsWith("precondition")) {
                return false;
            } else {
                return (file.getName().toLowerCase().endsWith("." + STORY_EXTENSION) || file.getName().toLowerCase().endsWith("." + FEATURE_EXTENSION));
            }
        };
    }

    private FileFilter thatAreNarratives() {
        return file -> file.getName().toLowerCase().equals("narrative.txt")
                || file.getName().toLowerCase().equals("narrative.md")
                || file.getName().toLowerCase().equals("readme.md")
                || file.getName().toLowerCase().equals("placeholder.txt");
    }

    private boolean isSupportedFileStoryExtension(String storyFileExtension) {
        return (storyFileExtension.toLowerCase().equals(FEATURE_EXTENSION) || storyFileExtension.toLowerCase().equals(STORY_EXTENSION));
    }

    public Optional<String> getOverview() {
        return overviewReader.readOverviewFrom(directoryPaths.toArray(new String[]{}));
    }

}
