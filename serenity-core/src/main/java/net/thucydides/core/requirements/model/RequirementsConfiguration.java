package net.thucydides.core.requirements.model;

import com.google.common.base.Splitter;
import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.requirements.RootDirectory;
import net.thucydides.core.requirements.SearchForFilesOfType;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class RequirementsConfiguration {
    public final static List<String> DEFAULT_CAPABILITY_TYPES = NewList.of("capability", "feature", "story");
    private static final String DEFAULT_ROOT_DIRECTORY = "stories";

    private final EnvironmentVariables environmentVariables;

    public RequirementsConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<String> getRequirementTypes() {
        String requirementTypes = ThucydidesSystemProperty.SERENITY_REQUIREMENT_TYPES.from(environmentVariables);
        List<String> types;
        if (StringUtils.isNotEmpty(requirementTypes)) {
            types = Splitter.on(",").trimResults().splitToList(requirementTypes);
        } else {
            types = getDefaultCapabilityTypes();
        }
        return types;
    }

    public String getDefaultRootDirectory() {
        if (ThucydidesSystemProperty.SERENITY_ANNOTATED_REQUIREMENTS_DIR.isDefinedIn(environmentVariables)) {
            return ThucydidesSystemProperty.SERENITY_ANNOTATED_REQUIREMENTS_DIR.from(environmentVariables);
        }
        return DEFAULT_ROOT_DIRECTORY;
    }

    public List<String> getDefaultCapabilityTypes() {
        if (jbehaveFilesExist()) {
            return jbehaveCapabilityTypes();
        }
        if (cucumberFilesExist()) {
            return cucumberCapabilityTypes();
        }

        return DEFAULT_CAPABILITY_TYPES;
    }

    private List<String> jbehaveCapabilityTypes() {
        int featureDirectoryDepth = getJBehaveFileMatcher().get().getMaxDepth();
        switch (featureDirectoryDepth) {
            case 0: return NewList.of("story");
            case 1: return NewList.of("feature", "story");
            default: return NewList.of("capability","feature", "story");
        }
    }

    private List<String> cucumberCapabilityTypes() {
        int featureDirectoryDepth = getCucumberFileMatcher().get().getMaxDepth();
        switch (featureDirectoryDepth) {
            case 0: return NewList.of("feature");
            case 1: return NewList.of("capability", "feature");
            default: return NewList.of("theme","capability", "feature");
        }
    }

    SearchForFilesOfType jbehaveFileMatcher;

    private Optional<SearchForFilesOfType> getJBehaveFileMatcher() {
        if (jbehaveFileMatcher != null) {
            return Optional.of(jbehaveFileMatcher);
        }
        try {
            Optional<Path> root = RootDirectory.definedIn(environmentVariables).featuresOrStoriesRootDirectory();// findResourcePath(rootRequirementsDirectory + "/stories");
            if (root.isPresent()) {
                jbehaveFileMatcher = new SearchForFilesOfType(root.get(),".story");
                Files.walkFileTree(root.get(), jbehaveFileMatcher);
                return Optional.of(jbehaveFileMatcher);
            }
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }


    public String getRequirementType(int level) {
        return RequirementTypeAt.level(level).in(getRequirementTypes());
    }
//
//    private Path findResourcePath(String path) throws URISyntaxException {
//        Path root;
//        URL storyDirectory = getClass().getResource(path);
//        if (storyDirectory != null) {
//            root =  Paths.get(storyDirectory.toURI());
//        } else {
//            root = Paths.get("src/test/resources/" + path);
//        }
//        return root;
//    }

    private SearchForFilesOfType cucumberFileMatcher;

    private Optional<SearchForFilesOfType> getCucumberFileMatcher() {
        if (cucumberFileMatcher != null) {
            return Optional.of(cucumberFileMatcher);
        }
        try {
            Optional<Path> root = RootDirectory.definedIn(environmentVariables).featuresOrStoriesRootDirectory();// findResourcePath(rootRequirementsDirectory + "/stories");
            if (root.isPresent()) {
                cucumberFileMatcher = new SearchForFilesOfType(root.get(),".feature");
                Files.walkFileTree(root.get(), cucumberFileMatcher);
                return Optional.of(cucumberFileMatcher);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.empty();
    }

    private boolean jbehaveFilesExist() {
        return (getJBehaveFileMatcher().isPresent() && getJBehaveFileMatcher().get().hasMatchingFiles());
    }

    private boolean cucumberFilesExist() {
        return (getCucumberFileMatcher().isPresent() && getCucumberFileMatcher().get().hasMatchingFiles());
    }

    public int startLevelForADepthOf(int requirementsDepth) {
        return Math.max(0, getRequirementTypes().size() - requirementsDepth);
    }
}
