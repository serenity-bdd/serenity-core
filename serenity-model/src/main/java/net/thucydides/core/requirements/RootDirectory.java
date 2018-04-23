package net.thucydides.core.requirements;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_FEATURES_DIRECTORY;
import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_STORIES_DIRECTORY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.replace;

/**
 * Find the root directory of the requirements hierarchy when using Cucumber or JBehave.
 * This is normally src/test/resources/features or src/test/resources/stories. For multi-module projects, it
 * can be a directory with this name in one of the modules. There should only be one requirements directory in a
 * multi-module project. The easiest approach is to have a dedicated module for the acceptance tests.
 *
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

    RootDirectory(EnvironmentVariables environmentVariables, String rootDirectoryPath) {
        this.environmentVariables = environmentVariables;
        this.rootDirectoryPath = rootDirectoryPath;

        this.featureDirectoryName = SERENITY_FEATURES_DIRECTORY.from(environmentVariables, DEFAULT_FEATURES_ROOT_DIRECTORY);
        this.storyDirectoryName = SERENITY_STORIES_DIRECTORY.from(environmentVariables, DEFAULT_STORIES_ROOT_DIRECTORY);
    }

    public static RootDirectory definedIn(EnvironmentVariables environmentVariables) {
        return new RootDirectory(environmentVariables,".");
    }
    /**
     * Find the root directory in the classpath or on the file system from which the requirements will be read.
     */
    public Set<String> getRootDirectoryPaths() {

        try {
            if (ThucydidesSystemProperty.SERENITY_TEST_REQUIREMENTS_BASEDIR.isDefinedIn(environmentVariables)) {
                return getRootDirectoryFromRequirementsBaseDir();
            } else {
                return firstDefinedOf(getRootDirectoryFromClasspath(),
                        getFileSystemDefinedDirectory(),
                        getRootDirectoryFromWorkingDirectory());
            }
        } catch (IOException e) {
            return new HashSet<>();
        }
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
        if (Paths.get(rootDirectoryPath).toAbsolutePath().toFile().exists()) {
            Set<String> directory = new HashSet<>();
            directory.add(Paths.get(rootDirectoryPath).toAbsolutePath().toString());
            return directory;
        }
        return new HashSet<>();
    }

    private Set<String> getRootDirectoryFromWorkingDirectory() {
        final String workingDirectory = System.getProperty("user.dir");
        final String mavenBuildDir = System.getProperty(SystemPropertiesConfiguration.PROJECT_BUILD_DIRECTORY);
        String resultDir = (!isEmpty(mavenBuildDir)) ? mavenBuildDir :  workingDirectory;
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
            if (new File(resourceDir, storyDirectoryName).exists()) {
                return Optional.of(resourceDir.toPath().resolve(storyDirectoryName));
            } else if (new File(resourceDir, featureDirectoryName).exists()) {
                return Optional.of(resourceDir.toPath().resolve(featureDirectoryName));
            }
        }
        return Optional.empty();
    }

    private static List<File> getResourceDirectories(Path root, EnvironmentVariables environmentVariables) {
        if (ThucydidesSystemProperty.SERENITY_REQUIREMENTS_DIR.isDefinedIn(environmentVariables)) {
            return new ArrayList<>();
        } else {
            return listDirectories(root).stream()
                    .filter(path -> path.endsWith("src/test/resources"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

    private static List<Path> listDirectories(Path path) {
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    files.add(entry);
                    files.addAll(listDirectories(entry));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }


}