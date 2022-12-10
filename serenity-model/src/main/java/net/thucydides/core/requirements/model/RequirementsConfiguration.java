package net.thucydides.core.requirements.model;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.requirements.DefaultCapabilityTypes;
import net.thucydides.core.requirements.RootDirectory;
import net.thucydides.core.util.EnvironmentVariables;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class RequirementsConfiguration {
    public final static List<String> DEFAULT_CAPABILITY_TYPES = NewList.of("capability", "feature", "story");
    private static final String DEFAULT_ROOT_DIRECTORY = "stories";

    private final EnvironmentVariables environmentVariables;
    private Optional<Path> root;

    public RequirementsConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        root = RootDirectory.definedIn(environmentVariables).featuresOrStoriesRootDirectory();
    }

    public RequirementsConfiguration(EnvironmentVariables environmentVariables, String rootDirectory) {
        this.environmentVariables = environmentVariables;
        root = Optional.of(absolutePathOfDirectoryOnClasspath(rootDirectory));
    }

    private Path absolutePathOfDirectoryOnClasspath(String rootDirectory) {
        URL rootDirOnClasspath = getClass().getClassLoader().getResource(rootDirectory);
        Path absolutePath = Paths.get(rootDirectory);
        if (rootDirOnClasspath != null) {
            try {
                absolutePath = Paths.get(rootDirOnClasspath.toURI());
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Fail to build absolute path of directory on classpath", e);
            }
        }
        return absolutePath;
    }

    public List<String> getRequirementTypes() {

        return DefaultCapabilityTypes.instance().getRequirementTypes(environmentVariables,root);
    }

    public String getDefaultRootDirectory() {
        if (ThucydidesSystemProperty.SERENITY_ANNOTATED_REQUIREMENTS_DIR.isDefinedIn(environmentVariables)) {
            return ThucydidesSystemProperty.SERENITY_ANNOTATED_REQUIREMENTS_DIR.from(environmentVariables);
        }
        return DEFAULT_ROOT_DIRECTORY;
    }

    public String getRequirementType(int level) {
        return RequirementTypeAt.level(level).in(getRequirementTypes());
    }

    public int startLevelForADepthOf(int requirementsDepth) {
        return Math.max(0, getRequirementTypes().size() - requirementsDepth);
    }
}
