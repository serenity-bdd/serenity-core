package net.thucydides.core.requirements;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isHidden;
import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_FEATURES_DIRECTORY;
import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_STORIES_DIRECTORY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.replace;

/**
 * Find the root directory of the requirements hierarchy when using Cucumber or JBehave.
 * This is normally src/test/resources/features or src/test/resources/stories. For multi-module projects, it
 * can be a directory with this name in one of the modules. There should only be one requirements directory in a
 * multi-module project. The easiest approach is to have a dedicated module for the acceptance tests.
 * <p>
 * You can hard-code this directory using serenity.requirements.dir. Milage may vary for multi-module projects.
 * If you need to override the root directory (e.g. to use src/test/resources/myFeatures), a better way is to
 * set the serenity.features.directory (for Cucumber) or serenity.stories.directory (for JBehave) property to
 * the simple name of the directory (e.g. serenity.features.directory=myFeatures).
 */
public class RootDirectory {
    private final EnvironmentVariables environmentVariables;
    private final String rootDirectoryPath;

    private final static String DEFAULT_FEATURES_ROOT_DIRECTORY = "features";
    private final static String DEFAULT_STORIES_ROOT_DIRECTORY = "stories";

    private final String featureDirectoryName;
    private final String storyDirectoryName;

    private static final Logger LOGGER = LoggerFactory.getLogger(RootDirectory.class);

    private final List<String> requirementsDirectoryNames;

    RootDirectory(EnvironmentVariables environmentVariables, String rootDirectoryPath) {
        this.environmentVariables = environmentVariables;
        this.rootDirectoryPath = rootDirectoryPath;

        List<String> customRequirementsDirectoryNames = new ArrayList<>();
        List<String> defaultRequirementsDirectoryNames = new ArrayList<>();


        this.featureDirectoryName = SERENITY_FEATURES_DIRECTORY.from(environmentVariables, DEFAULT_FEATURES_ROOT_DIRECTORY);
        this.storyDirectoryName = SERENITY_STORIES_DIRECTORY.from(environmentVariables, DEFAULT_STORIES_ROOT_DIRECTORY);

        if (SERENITY_STORIES_DIRECTORY.isDefinedIn(environmentVariables)) {
            customRequirementsDirectoryNames.add(storyDirectoryName);
        } else {
            defaultRequirementsDirectoryNames.add(storyDirectoryName);
        }
        if (SERENITY_FEATURES_DIRECTORY.isDefinedIn(environmentVariables)) {
            customRequirementsDirectoryNames.add(featureDirectoryName);
        } else {
            defaultRequirementsDirectoryNames.add(featureDirectoryName);
        }

        requirementsDirectoryNames = new ArrayList<>(customRequirementsDirectoryNames);
        requirementsDirectoryNames.addAll(defaultRequirementsDirectoryNames);

    }

    public Set<String> requirementsDirectoryNames() {
        return new HashSet<>(requirementsDirectoryNames);
    }

    public static RootDirectory definedIn(EnvironmentVariables environmentVariables) {
        return new RootDirectory(environmentVariables, ".");
    }

    /**
     * Find the root directory in the classpath or on the file system from which the requirements will be read.
     */
    public Set<String> getRootDirectoryPaths() {

        Set<String> rootDirectories;
        try {
            if (ThucydidesSystemProperty.SERENITY_TEST_REQUIREMENTS_BASEDIR.isDefinedIn(environmentVariables)) {
                rootDirectories = getRootDirectoryFromRequirementsBaseDir();
            } else {
                rootDirectories = firstDefinedOf(
                        getRootDirectoryFromClasspath(),
                        getGradleProjectDirectoryAsSet(),
                        getFileSystemDefinedDirectory(),
                        getRootDirectoryFromWorkingDirectory()
                );
            }

            return rootDirectories.stream().map(path -> toAbsolute(path)).collect(Collectors.toSet());
        } catch (IOException e) {
            return new HashSet<>();
        }
    }

    private String toAbsolute(String path) {

        if (Paths.get(WindowsFriendly.formOf(path)).isAbsolute()) {
            return path;
        }
        return Paths.get(System.getProperty("user.dir")).resolve(path).toString();
    }

    public String featureDirectoryName() {
        return featureDirectoryName;
    }

    public String storyDirectoryName() {
        return storyDirectoryName;
    }

    @SafeVarargs
    private final Set<String> firstDefinedOf(Set<String>... paths) {
        for (Set<String> path : paths) {
            if (!path.isEmpty()) {
                return path;
            }
        }
        return new HashSet<>();
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
        Set<String> urlsWithRestoredSpaces = new HashSet<>();
        for (URL resourceRoot : resourceRoots) {
            urlsWithRestoredSpaces.add(withRestoredSpaces(resourceRoot.getPath()));
        }
        return urlsWithRestoredSpaces;
    }

    private String withRestoredSpaces(String path) {
        try {
            return URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return replace(path, "%20", " ");
        }
    }

    private Set<String> getFileSystemDefinedDirectory() {
        File rootDirectoryPathFile = FileSystems.getDefault().getPath(rootDirectoryPath).toFile();

        if (rootDirectoryPathFile.exists()) {
            Set<String> directory = new HashSet<>();
            directory.add(rootDirectoryPathFile.getPath());
            return directory;
        }
        return new HashSet<>();
    }

    private Set<String> getGradleProjectDirectoryAsSet() {

        String gradleProjectDir = getGradleProjectDirectory();
        String gradleResourcetDir = new File(gradleProjectDir, rootDirectoryPath).getAbsolutePath();

        if (gradleProjectDir != null) {
            Set<String> directory = new HashSet<>();
            directory.add(gradleResourcetDir);
            return directory;
        }
        return new HashSet<>();
    }

    private String getGradleProjectDirectory() {
        return environmentVariables.getProperty("serenity.project.directory");
    }

    private Set<String> getRootDirectoryFromWorkingDirectory() {
        final String workingDirectory = System.getProperty("user.dir");
        final String mavenBuildDir = System.getProperty(SystemPropertiesConfiguration.PROJECT_BUILD_DIRECTORY);
        final String gradleBuildDir = getGradleProjectDirectory();
        String resultDir = (!isEmpty(mavenBuildDir)) ? mavenBuildDir : (!isEmpty(gradleBuildDir)) ? gradleBuildDir : workingDirectory;

        return getRootDirectoryFromParentDir(resultDir);
    }

    private Set<String> configuredRelativeRootDirectories;

    private Set<String> getRootDirectoryFromRequirementsBaseDir() {

        if (configuredRelativeRootDirectories == null) {
            configuredRelativeRootDirectories
                    = getRootDirectoryFromParentDir(ThucydidesSystemProperty.SERENITY_TEST_REQUIREMENTS_BASEDIR
                    .from(environmentVariables, ""));
        }
        return configuredRelativeRootDirectories;
    }

    private Set<String> getRootDirectoryFromParentDir(String parentDir) {
        List<File> resourceDirectories = getResourceDirectories(Paths.get(parentDir), environmentVariables);

        Set<String> directoryPaths = new HashSet<>();

        for (File resourceDirectory : resourceDirectories) {

            if (new File(resourceDirectory, rootDirectoryPath).exists()) {
                directoryPaths.add(new File(resourceDirectory, rootDirectoryPath).getAbsolutePath()); //custom absolute requirements directory
            }
            if (new File(resourceDirectory, featureDirectoryName).exists()) {
                directoryPaths.add(new File(resourceDirectory, featureDirectoryName).getAbsolutePath()); //features
            }
            if (new File(resourceDirectory, storyDirectoryName).exists()) {
                directoryPaths.add(new File(resourceDirectory, storyDirectoryName).getAbsolutePath()); //stories
            }
        }
        return directoryPaths;
    }

    private Enumeration<URL> getDirectoriesFrom(String root) throws IOException, URISyntaxException {
        String rootWithEscapedSpaces = root.replaceAll(" ", "%20");
        URI rootUri = (isWindowsPath(rootWithEscapedSpaces)) ? new File(root).toPath().toUri() : new URI(rootWithEscapedSpaces);
        return getClass().getClassLoader().getResources(rootUri.getPath());
    }


    private static final Pattern WINDOWS_PATH = Pattern.compile("([a-zA-Z]:)?(\\\\[a-zA-Z0-9_-]+)+\\\\?");

    private boolean isWindowsPath(String rootWithEscapedSpaces) {
        return WINDOWS_PATH.matcher(rootWithEscapedSpaces).find();
    }

    public Optional<Path> featuresOrStoriesRootDirectory() {

        String relativeRoot = rootDirectoryPath.equals(".") ? "" : rootDirectoryPath;

        if (ThucydidesSystemProperty.SERENITY_REQUIREMENTS_DIR.isDefinedIn(environmentVariables)) {
            return Optional.of(Paths.get(ThucydidesSystemProperty.SERENITY_REQUIREMENTS_DIR.from(environmentVariables)));
        }
        List<File> resourceDirectories = getResourceDirectories(Paths.get(relativeRoot), environmentVariables);
        for (File resourceDir : resourceDirectories) {
            for (String candidateDirectoryName : requirementsDirectoryNames) {
                if (new File(resourceDir, candidateDirectoryName).exists()) {
                    return Optional.of(resourceDir.toPath().resolve(candidateDirectoryName));
                }
            }
        }
        return Optional.empty();
    }

    private static Map<Path, List<File>> RESOURCE_DIRECTORY_CACHE = new HashMap<>();

    private static List<File> getResourceDirectories(Path root, EnvironmentVariables environmentVariables) {

        if (RESOURCE_DIRECTORY_CACHE.containsKey(root)) {
            return RESOURCE_DIRECTORY_CACHE.get(root);
        }

        List<File> results;
        if (ThucydidesSystemProperty.SERENITY_REQUIREMENTS_DIR.isDefinedIn(environmentVariables)) {
            results = new ArrayList<>();
        } else {
            results = listDirectories(root).parallelStream()
                    .filter(path -> path.endsWith("src/test/resources"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
        RESOURCE_DIRECTORY_CACHE.put(root, results);

        return results;
    }

    private static List<Path> listDirectories(Path path) {
        List<Path> files = new ArrayList<>();
        if (isResourceDirectoryCandidate(path)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    if (isResourceDirectoryCandidate(entry)) {
                        files.add(entry);
                        files.addAll(listDirectories(entry));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return files;
    }

    /**
     * Don't bother looking for src/test/resources folders in directories with names like these
     */
    private final static List<Predicate<Path>> IGNORED_DIRECTORIES = new ArrayList<>();

    static {
        IGNORED_DIRECTORIES.add(path -> path.getFileName().toString().startsWith("."));
        IGNORED_DIRECTORIES.add(path -> path.getFileName().toString().equals("target"));
        IGNORED_DIRECTORIES.add(path -> path.getFileName().toString().equals("build"));
        IGNORED_DIRECTORIES.add(path -> path.getFileName().toString().equals("out"));
        IGNORED_DIRECTORIES.add(path -> path.getFileName().toString().equals("java"));
        IGNORED_DIRECTORIES.add(path -> path.getFileName().toString().equals("scala"));
        IGNORED_DIRECTORIES.add(path -> path.getFileName().toString().equals("groovy"));
        IGNORED_DIRECTORIES.add(path -> path.getFileName().toString().equals("kotlin"));
        IGNORED_DIRECTORIES.add(path -> path.getFileName().toString().equals("features"));
        IGNORED_DIRECTORIES.add(path -> path.getFileName().toString().equals("stories"));
    }

    private static boolean isResourceDirectoryCandidate(Path entry) {

        try {
            if (entry.toString().isEmpty()) {
                return true;
            }
            if (!isDirectory(entry)) {
                return false;
            }
            if (isHidden(entry)) {
                return false;
            }

            return IGNORED_DIRECTORIES.stream().noneMatch(
                    shouldIgnore -> shouldIgnore.test(entry)
            );

        } catch (IOException e) {
            return false;
        }
    }


    Path getRelativePathOf(String path) {
        if (path == null) {
            return Paths.get("");
        }
        for (String requirementsDirectory : requirementsDirectoryNames) {
            if (path.startsWith("classpath:" + requirementsDirectory + "/")) {
                return Paths.get(path.substring(requirementsDirectory.length() + 11));
            } else if (relativePathFromAbsolutePath(path, requirementsDirectory).isPresent()) {
                return relativePathFromAbsolutePath(path, requirementsDirectory).get();
            }
        }
        if (path.startsWith("classpath:")) {
            return Paths.get(path.substring(10));
        }
        return new File(path).toPath();
    }

    private Optional<Path> relativePathFromAbsolutePath(String absolutePath, String requirementsDirectory) {
        if (absolutePath.startsWith("file:/")) {
            String requirementsDirectoryInPath = "/" + requirementsDirectory + "/";
            if (absolutePath.contains(requirementsDirectoryInPath)) {
                int startOfRelativePath = absolutePath.lastIndexOf(requirementsDirectoryInPath) + requirementsDirectoryInPath.length();
                return Optional.of(Paths.get(absolutePath.substring(startOfRelativePath)));
            }
        } else if (absolutePath.startsWith("file:")) {
            return Optional.of(Paths.get(absolutePath.substring(5)));
        }
        return Optional.empty();
    }
}