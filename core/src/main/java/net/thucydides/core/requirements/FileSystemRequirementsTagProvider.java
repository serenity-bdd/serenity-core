package net.thucydides.core.requirements;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.Narrative;
import net.thucydides.core.requirements.model.NarrativeReader;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Pattern;

import static ch.lambdaj.Lambda.convert;
import static net.thucydides.core.requirements.RequirementsPath.pathElements;
import static net.thucydides.core.requirements.RequirementsPath.stripRootFromPath;
import static net.thucydides.core.util.NameConverter.humanize;

//import javax.persistence.Transient;

/**
 * Load a set of requirements (epics/themes,...) from the directory structure.
 * This will typically be the directory structure containing the tests (for JUnit) or stories (e.g. for JBehave).
 * By default, the tests
 */
public class FileSystemRequirementsTagProvider extends AbstractRequirementsTagProvider implements RequirementsTagProvider, OverridableTagProvider {

    private final static String DEFAULT_ROOT_DIRECTORY = "stories";
    private final static String FEATURES_ROOT_DIRECTORY = "features";
    private final static String DEFAULT_RESOURCE_DIRECTORY = "src/test/resources";
    private static final String WORKING_DIR = "user.dir";
    private static final List<Requirement> NO_REQUIREMENTS = Lists.newArrayList();
    private static final List<TestTag> NO_TEST_TAGS = Lists.newArrayList();
    public static final String STORY_EXTENSION = "story";
    public static final String FEATURE_EXTENSION = "feature";

    private final String rootDirectoryPath;
    private final NarrativeReader narrativeReader;
    private final int level;

    //    @Transient
    private List<Requirement> requirements;

    public FileSystemRequirementsTagProvider() {
        this(defaultRootDirectoryPathFrom(Injectors.getInjector().getProvider(EnvironmentVariables.class).get()));
    }

    public static String defaultRootDirectoryPathFrom(EnvironmentVariables environmentVariables) {

        if (ThucydidesSystemProperty.THUCYDIDES_REQUIREMENTS_DIR.isDefinedIn(environmentVariables)) {
            return ThucydidesSystemProperty.THUCYDIDES_REQUIREMENTS_DIR.from(environmentVariables);
        }
        if (ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT.isDefinedIn(environmentVariables)) {
            return ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT.from(environmentVariables);
        }

        Optional<String> resourceDirectory = getResourceDirectory(environmentVariables);
        if(resourceDirectory.isPresent()) {
            String resourceDir =  resourceDirectory.get();
            if(new File(resourceDir,DEFAULT_ROOT_DIRECTORY).exists()) {
                return DEFAULT_ROOT_DIRECTORY;
            } else if(new File(resourceDir, FEATURES_ROOT_DIRECTORY).exists()) {
                return FEATURES_ROOT_DIRECTORY;
            }
        }
        return DEFAULT_ROOT_DIRECTORY;
    }

    public FileSystemRequirementsTagProvider(String rootDirectory, int level) {
        this(filePathFormOf(rootDirectory), level, Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
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

    public FileSystemRequirementsTagProvider(String rootDirectory, int level, EnvironmentVariables environmentVariables) {
        super(environmentVariables);
        this.rootDirectoryPath = rootDirectory;
        this.level = level;
        this.narrativeReader = NarrativeReader.forRootDirectory(rootDirectory)
                .withRequirementTypes(getRequirementTypes());
    }

    public FileSystemRequirementsTagProvider(String rootDirectory) {
        this(rootDirectory, 0);
    }

    /**
     * We look for file system requirements in the root directory path (by default, 'stories').
     * First, we look on the classpath. If we don't find anything on the classpath (e.g. if the task is
     * being run from the Maven plugin), we look in the src/main/resources and src/test/resources directories starting
     * at the working directory.
     */
    public List<Requirement> getRequirements() {
        if (requirements == null) {
            try {
                Set<Requirement> allRequirements = Sets.newHashSet();
                Set<String> directoryPaths = getRootDirectoryPaths();
                for(String rootDirectoryPath : directoryPaths) {
                    File rootDirectory = new File(rootDirectoryPath);
                    allRequirements.addAll(loadCapabilitiesFrom(rootDirectory.listFiles(thatAreDirectories())));
                    allRequirements.addAll(loadStoriesFrom(rootDirectory.listFiles(thatAreStories())));
                }
                requirements = Lists.newArrayList(allRequirements);
                Collections.sort(requirements);
            } catch (IOException e) {
                requirements = NO_REQUIREMENTS;
                throw new IllegalArgumentException("Could not load requirements from '" + rootDirectoryPath + "'", e);
            }
            if (level == 0) {
                requirements = addParentsTo(requirements);
            }
        }
        return requirements;
    }

    private List<Requirement> addParentsTo(List<Requirement> requirements) {
        return addParentsTo(requirements, null);
    }

    private List<Requirement> addParentsTo(List<Requirement> requirements, String parent) {
        List<Requirement> augmentedRequirements = Lists.newArrayList();
        for (Requirement requirement : requirements) {
            List<Requirement> children = requirement.hasChildren()
                    ? addParentsTo(requirement.getChildren(), requirement.getName()) : NO_REQUIREMENTS;
            augmentedRequirements.add(requirement.withParent(parent).withChildren(children));
        }
        return augmentedRequirements;
    }

    /**
     * Find the root directory in the classpath or on the file system from which the requirements will be read.
     */
    public Set<String> getRootDirectoryPaths() throws IOException {

        if (ThucydidesSystemProperty.THUCYDIDES_TEST_REQUIREMENTS_BASEDIR.isDefinedIn(environmentVariables)) {
            return getRootDirectoryFromRequirementsBaseDir().asSet();
        } else {
            Set<String> rootDirectoryOnClasspath = getRootDirectoryFromClasspath();
            if (!rootDirectoryOnClasspath.isEmpty()) {
                return rootDirectoryOnClasspath;
            } else {
                return getRootDirectoryFromWorkingDirectory();
            }
        }
    }

    private Set<String> getRootDirectoryFromClasspath() throws IOException {
        List<URL> resourceRoots;
        try {
            Enumeration<URL> requirementResources = getDirectoriesFrom(rootDirectoryPath);
            resourceRoots = Collections.list(requirementResources);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        return restoreSpacesIn(resourceRoots);
    }

    private Set<String> restoreSpacesIn(List<URL> resourceRoots) {
        Set<String> urlsWithRestoredSpaces = Sets.newHashSet();
        for(URL resourceRoot : resourceRoots) {
            urlsWithRestoredSpaces.add(withRestoredSpaces(resourceRoot.getPath()));
        }
        return urlsWithRestoredSpaces;
    }

    private String withRestoredSpaces(String path) {
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return StringUtils.replace(path, "%20", " ");
        }
    }

    private Set<String> getRootDirectoryFromWorkingDirectory() throws IOException {
        return getRootDirectoryFromParentDir(System.getProperty(WORKING_DIR)).asSet();
    }

    private Optional<String> configuredRelativeRootDirectory;

    private Optional<String> getRootDirectoryFromRequirementsBaseDir() {
        if (configuredRelativeRootDirectory == null) {
            configuredRelativeRootDirectory
                    = getRootDirectoryFromParentDir(ThucydidesSystemProperty.THUCYDIDES_TEST_REQUIREMENTS_BASEDIR
                    .from(environmentVariables, ""));
        }
        return configuredRelativeRootDirectory;
    }

    private Optional<String> getRootDirectoryFromParentDir(String parentDir) {
        File resourceDirectory = getResourceDirectory(environmentVariables).isPresent() ? new File(parentDir, getResourceDirectory(environmentVariables).get()) : new File(parentDir);
        File requirementsDirectory = absolutePath(rootDirectoryPath) ? new File(rootDirectoryPath) : new File(resourceDirectory, rootDirectoryPath);
        if(!requirementsDirectory.exists()) {
            requirementsDirectory = new File(resourceDirectory, FEATURES_ROOT_DIRECTORY); //features
        }
        if (requirementsDirectory.exists()) {
            return Optional.of(requirementsDirectory.getAbsolutePath());
        } else {
            return Optional.absent();
        }
    }

    private boolean absolutePath(String rootDirectoryPath) {
        return (new File(rootDirectoryPath).isAbsolute() || rootDirectoryPath.startsWith("/"));
    }


    private Enumeration<URL> getDirectoriesFrom(String root) throws IOException, URISyntaxException {
        String rootWithEscapedSpaces = root.replaceAll(" ", "%20");
        URI rootUri = (isWindowsPath(rootWithEscapedSpaces)) ? new File(root).toPath().toUri() : new URI(rootWithEscapedSpaces);
        return getClass().getClassLoader().getResources(rootUri.getPath());
    }

    private final Pattern WINDOWS_PATH = Pattern.compile("([a-zA-Z]:)?(\\\\[a-zA-Z0-9_-]+)+\\\\?");

    private boolean isWindowsPath(String rootWithEscapedSpaces) {
        return WINDOWS_PATH.matcher(rootWithEscapedSpaces).find();
    }

    public Set<TestTag> getTagsFor(final TestOutcome testOutcome) {
        Set<TestTag> tags = new HashSet<>();
        if (testOutcome.getPath() != null) {
            List<String> storyPathElements = stripRootFrom(pathElements(stripRootPathFrom(testOutcome.getPath())));
            addStoryTagIfPresent(tags, storyPathElements);
            storyPathElements = stripStorySuffixFrom(storyPathElements);
            tags.addAll(getMatchingCapabilities(getRequirements(), storyPathElements));
        }
        return tags;
    }

    private List<String> stripStorySuffixFrom(List<String> pathElements) {
        if ((!pathElements.isEmpty()) && isSupportedFileStoryExtension(last(pathElements))) {
            return dropLastElement(pathElements);
        } else {
            return pathElements;
        }
    }

    private List<String> dropLastElement(List<String> pathElements) {
        List<String> strippedPathElements = Lists.newArrayList(pathElements);
        strippedPathElements.remove(pathElements.size() - 1);
        return strippedPathElements;
    }

    private void addStoryTagIfPresent(Set<TestTag> tags, List<String> storyPathElements) {
        Optional<TestTag> storyTag = storyTagFrom(storyPathElements);
        tags.addAll(storyTag.asSet());
    }

    private Optional<TestTag> storyTagFrom(List<String> storyPathElements) {
        if ((!storyPathElements.isEmpty()) && isSupportedFileStoryExtension(last(storyPathElements))) {
            String storyName = Lists.reverse(storyPathElements).get(1);
            String storyParent = parentElement(storyPathElements);
            String qualifiedName = storyParent == null ?
                    humanize(storyName) : humanize(storyParent).trim() + "/" + humanize(storyName);
            TestTag storyTag = TestTag.withName(qualifiedName).andType("story");
            return Optional.of(storyTag);
        } else {
            return Optional.absent();
        }
    }

    private String parentElement(List<String> storyPathElements) {
        return storyPathElements.size() > 2 ? Lists.reverse(storyPathElements).get(2) : null;
    }

    private String last(List<String> list) {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }

    public Optional<Requirement> getParentRequirementOf(final TestOutcome testOutcome) {
        if (testOutcome.getPath() != null) {
            List<String> storyPathElements = stripStorySuffixFrom(stripRootFrom(pathElements(stripRootPathFrom(testOutcome.getPath()))));
            return lastRequirementFrom(storyPathElements);
        } else {
            return mostSpecificTagRequirementFor(testOutcome);
        }
    }

    private Optional<Requirement> mostSpecificTagRequirementFor(TestOutcome testOutcome) {
        Optional<Requirement> mostSpecificRequirement = Optional.absent();
        int currentSpecificity = -1;

        for (TestTag tag : testOutcome.getTags()) {
            Optional<Requirement> matchingRequirement = getRequirementFor(tag);
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

    public Optional<Requirement> getRequirementFor(TestTag testTag) {
        for (Requirement requirement : getFlattenedRequirements()) {
            if (requirement.getName().equalsIgnoreCase(testTag.getName()) && requirement.getType().equalsIgnoreCase(testTag.getType())) {
                return Optional.of(requirement);
            }
        }
        return Optional.absent();
    }

    private List<Requirement> getFlattenedRequirements() {
        List<Requirement> allRequirements = Lists.newArrayList();
        for (Requirement requirement : getRequirements()) {
            allRequirements.add(requirement);
            allRequirements.addAll(childRequirementsOf(requirement));
        }
        return allRequirements;
    }

    private Collection<Requirement> childRequirementsOf(Requirement requirement) {
        List<Requirement> childRequirements = Lists.newArrayList();
        for (Requirement childRequirement : requirement.getChildren()) {
            childRequirements.add(childRequirement);
            childRequirements.addAll(childRequirementsOf(childRequirement));
        }
        return childRequirements;
    }

    private Optional<Requirement> lastRequirementFrom(List<String> storyPathElements) {
        if (storyPathElements.isEmpty()) {
            return Optional.absent();
        } else {
            return lastRequirementMatchingPath(getRequirements(), storyPathElements);
        }
    }

    private Optional<Requirement> lastRequirementMatchingPath(List<Requirement> requirements, List<String> storyPathElements) {
        if (storyPathElements.isEmpty()) {
            return Optional.absent();
        }
        Optional<Requirement> matchingRequirement = findMatchingRequirementIn(next(storyPathElements), requirements);
        if (!matchingRequirement.isPresent()) {
            return Optional.absent();
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
            Optional<Requirement> matchingRequirement = findMatchingRequirementIn(next(storyPathElements), requirements);
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
        return stripRootFromPath(rootDirectoryPath, storyPathElements);
    }

    private String stripRootPathFrom(String testOutcomePath) {
        String rootPath = ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT.from(environmentVariables);
        if (rootPath != null && testOutcomePath.startsWith(rootPath) && (!testOutcomePath.equals(rootPath))) {
            return testOutcomePath.substring(rootPath.length() + 1);
        } else {
            return testOutcomePath;
        }
    }

    private List<TestTag> concat(TestTag thisTag, List<TestTag> remainingTags) {
        List<TestTag> totalTags = new ArrayList<TestTag>();
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

    private Optional<Requirement> findMatchingRequirementIn(String storyPathElement, List<Requirement> requirements) {
        for (Requirement requirement : requirements) {
            String normalizedStoryPathElement = Inflector.getInstance().humanize(Inflector.getInstance().underscore(storyPathElement));
            if (requirement.getName().equals(normalizedStoryPathElement)) {
                return Optional.of(requirement);
            }
        }
        return Optional.absent();
    }

    private List<Requirement> loadCapabilitiesFrom(File[] requirementDirectories) {
        return convert(requirementDirectories, toRequirements());
    }


    private List<Requirement> loadStoriesFrom(File[] storyFiles) {
        return convert(storyFiles, toStoryRequirements());
    }

    private Converter<File, Requirement> toRequirements() {
        return new Converter<File, Requirement>() {

            public Requirement convert(File requirementFileOrDirectory) {
                return readRequirementFrom(requirementFileOrDirectory);
            }
        };
    }

    private Converter<File, Requirement> toStoryRequirements() {
        return new Converter<File, Requirement>() {

            public Requirement convert(File storyFile) {
                return readRequirementsFromStoryFile(storyFile);
            }
        };
    }

    private Requirement readRequirementFrom(File requirementDirectory) {
        Optional<Narrative> requirementNarrative = narrativeReader.loadFrom(requirementDirectory, level);

        if (requirementNarrative.isPresent()) {
            return requirementWithNarrative(requirementDirectory,
                    humanReadableVersionOf(requirementDirectory.getName()),
                    requirementNarrative.get());
        } else {
            return requirementFromDirectoryName(requirementDirectory);
        }
    }

    private Requirement readRequirementsFromStoryFile(File storyFile) {
        Optional<Narrative> optionalNarrative = narrativeReader.loadFromStoryFile(storyFile);
        String storyFileName = storyFile.getName();
        String storyName = "";
        String storyType = "story";
        if(storyFileName.endsWith("." + STORY_EXTENSION)) {
            storyName = storyFile.getName().replace("." + STORY_EXTENSION, "");
            storyType = "story";
        }
        else if(storyFileName.endsWith("." + FEATURE_EXTENSION)) {
            storyName = storyFile.getName().replace("." + FEATURE_EXTENSION, "");
            storyType = "feature";
        }
        if (optionalNarrative.isPresent()) {
            return requirementWithNarrative(storyFile, humanReadableVersionOf(storyName), optionalNarrative.get()).withType(storyType);
        } else {
            return storyNamed(storyName).withType(storyType);
        }
    }

    private Requirement requirementFromDirectoryName(File requirementDirectory) {
        String shortName = humanReadableVersionOf(requirementDirectory.getName());
        List<Requirement> children = readChildrenFrom(requirementDirectory);
        return Requirement.named(shortName).withType(getDefaultType(level)).withNarrative(shortName).withChildren(children);
    }

    private Requirement storyNamed(String storyName) {
        String shortName = humanReadableVersionOf(storyName);
        return Requirement.named(shortName).withType(STORY_EXTENSION).withNarrative(shortName);
    }



    private Requirement requirementWithNarrative(File requirementDirectory, String shortName, Narrative requirementNarrative) {
        String displayName = getTitleFromNarrativeOrDirectoryName(requirementNarrative, shortName);
        String cardNumber = requirementNarrative.getCardNumber().orNull();
        String type = requirementNarrative.getType();
        List<String> releaseVersions = requirementNarrative.getVersionNumbers();
        List<Requirement> children = readChildrenFrom(requirementDirectory);
        return Requirement.named(shortName)
                .withOptionalDisplayName(displayName)
                .withOptionalCardNumber(cardNumber)
                .withType(type)
                .withNarrative(requirementNarrative.getText())
                .withReleaseVersions(releaseVersions)
                .withChildren(children);
    }

    private List<Requirement> readChildrenFrom(File requirementDirectory) {
        String childDirectory = rootDirectoryPath + "/" + requirementDirectory.getName();
        RequirementsTagProvider childReader = new FileSystemRequirementsTagProvider(childDirectory, level + 1, environmentVariables);
        return childReader.getRequirements();
    }

    private String getTitleFromNarrativeOrDirectoryName(Narrative requirementNarrative, String nameIfNoNarrativePresent) {
        if (requirementNarrative.getTitle().isPresent()) {
            return requirementNarrative.getTitle().get();
        } else {
            return nameIfNoNarrativePresent;
        }
    }

    private FileFilter thatAreDirectories() {
        return new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() && !file.getName().startsWith(".");
            }
        };
    }

    private FileFilter thatAreStories() {
        return new FileFilter() {
            public boolean accept(File file) {
                String filename = file.getName().toLowerCase();
                if (filename.startsWith("given") || filename.startsWith("precondition")) {
                    return false;
                } else {
                    return (file.getName().toLowerCase().endsWith("." + STORY_EXTENSION) || file.getName().toLowerCase().endsWith("." + FEATURE_EXTENSION));
                }
            }
        };
    }

    public static Optional<String> getResourceDirectory(EnvironmentVariables environmentVariables) {
        if (ThucydidesSystemProperty.THUCYDIDES_REQUIREMENTS_DIR.isDefinedIn(environmentVariables)) {
            return Optional.absent();
        } else {
            return Optional.of(DEFAULT_RESOURCE_DIRECTORY);
        }
    }

    private boolean isSupportedFileStoryExtension(String storyFileExtension) {
        return (storyFileExtension.toLowerCase().equals(FEATURE_EXTENSION) || storyFileExtension.toLowerCase().equals(STORY_EXTENSION));
    }
}
