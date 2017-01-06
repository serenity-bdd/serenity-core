package net.thucydides.core.requirements;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class RootDirectory {
    private final EnvironmentVariables environmentVariables;
    private final String rootDirectoryPath;

    private final static String FEATURES_ROOT_DIRECTORY = "features";
    private final static String STORIES_ROOT_DIRECTORY = "stories";
    private final static String DEFAULT_RESOURCE_DIRECTORY = "src/test/resources";

    public RootDirectory(EnvironmentVariables environmentVariables) {
        this(environmentVariables, defaultRootDirectoryPathFrom(Injectors.getInjector().getProvider(EnvironmentVariables.class).get()));
    }

    public RootDirectory(EnvironmentVariables environmentVariables, String rootDirectoryPath) {
        this.environmentVariables = environmentVariables;
        this.rootDirectoryPath = rootDirectoryPath;
    }

    /**
     * Find the root directory in the classpath or on the file system from which the requirements will be read.
     */
    public Set<String> getRootDirectoryPaths() {

        try {
            if (ThucydidesSystemProperty.THUCYDIDES_TEST_REQUIREMENTS_BASEDIR.isDefinedIn(environmentVariables)) {
                return getRootDirectoryFromRequirementsBaseDir();
            } else {
                return firstDefinedOf(getRootDirectoryFromClasspath(),
                        getFileSystemDefinedDirectory(),
                        getRootDirectoryFromWorkingDirectory());
            }
        } catch(IOException e) {
            return new HashSet<>();
        }
    }

    private Set<String> firstDefinedOf(Set<String>... paths) {
        for (Set<String> path : paths) {
            if (!path.isEmpty()) {
                return path;
            }
        }
        return Sets.newHashSet();
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
        for (URL resourceRoot : resourceRoots) {
            urlsWithRestoredSpaces.add(withRestoredSpaces(resourceRoot.getPath()));
        }
        return urlsWithRestoredSpaces;
    }

    private String withRestoredSpaces(String path) {
        try {
            return URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return StringUtils.replace(path, "%20", " ");
        }
    }

    private Set<String> getFileSystemDefinedDirectory() throws IOException {
        if (Paths.get(rootDirectoryPath).toAbsolutePath().toFile().exists()) {
            return Sets.newHashSet(Paths.get(rootDirectoryPath).toAbsolutePath().toString());
        }
        return Sets.newHashSet();
    }

    private Set<String> getRootDirectoryFromWorkingDirectory() throws IOException {
        final String workingDirectory = System.getProperty("user.dir");
        final String mavenBuildDir = System.getProperty(SystemPropertiesConfiguration.PROJECT_BUILD_DIRECTORY);
        String resultDir = "";
        if (!StringUtils.isEmpty(mavenBuildDir)) {
            resultDir = mavenBuildDir;
        } else {
            resultDir = workingDirectory;
        }
        return getRootDirectoryFromParentDir(resultDir);
    }

    private Set<String> configuredRelativeRootDirectories;

    private Set<String> getRootDirectoryFromRequirementsBaseDir() {
        if (configuredRelativeRootDirectories == null) {
            configuredRelativeRootDirectories
                    = getRootDirectoryFromParentDir(ThucydidesSystemProperty.THUCYDIDES_TEST_REQUIREMENTS_BASEDIR
                    .from(environmentVariables, ""));
        }
        return configuredRelativeRootDirectories;
    }

    private Set<String> getRootDirectoryFromParentDir(String parentDir) {
        File resourceDirectory = getResourceDirectory(environmentVariables).isPresent() ? new File(parentDir, getResourceDirectory(environmentVariables).get()) : new File(parentDir);
        File requirementsDirectory = absolutePath(rootDirectoryPath) ? new File(rootDirectoryPath) : new File(resourceDirectory, rootDirectoryPath);

        Set<String> directoryPaths = Sets.newHashSet();

        if (requirementsDirectory.exists()) {
            directoryPaths.add(requirementsDirectory.getAbsolutePath());
        }
        // Add configured directory if it exists
        if (new File(resourceDirectory, FEATURES_ROOT_DIRECTORY).exists()) {
            directoryPaths.add(new File(resourceDirectory, FEATURES_ROOT_DIRECTORY).getAbsolutePath()); //features
        }
        if (new File(resourceDirectory, STORIES_ROOT_DIRECTORY).exists()) {
            directoryPaths.add(new File(resourceDirectory, FEATURES_ROOT_DIRECTORY).getAbsolutePath()); //features
        }

        return directoryPaths;
    }

    private boolean absolutePath(String rootDirectoryPath) {
        return (new File(rootDirectoryPath).isAbsolute() || rootDirectoryPath.startsWith("/"));
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

    public static String defaultRootDirectoryPathFrom(EnvironmentVariables environmentVariables) {

        if (ThucydidesSystemProperty.THUCYDIDES_REQUIREMENTS_DIR.isDefinedIn(environmentVariables)) {
            return ThucydidesSystemProperty.THUCYDIDES_REQUIREMENTS_DIR.from(environmentVariables);
        }
        Optional<String> resourceDirectory = getResourceDirectory(environmentVariables);
        if (resourceDirectory.isPresent()) {
            String resourceDir = resourceDirectory.get();
            if (new File(resourceDir, STORIES_ROOT_DIRECTORY).exists()) {
                return STORIES_ROOT_DIRECTORY;
            } else if (new File(resourceDir, FEATURES_ROOT_DIRECTORY).exists()) {
                return FEATURES_ROOT_DIRECTORY;
            }
        }
        return STORIES_ROOT_DIRECTORY;
    }

    public static Optional<String> getResourceDirectory(EnvironmentVariables environmentVariables) {
        if (ThucydidesSystemProperty.THUCYDIDES_REQUIREMENTS_DIR.isDefinedIn(environmentVariables)) {
            return Optional.absent();
        } else {
            return Optional.of(DEFAULT_RESOURCE_DIRECTORY);
        }
    }


}