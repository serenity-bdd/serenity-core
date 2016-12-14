package net.thucydides.core.requirements.model;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.requirements.SearchForFilesOfType;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class RequirementsConfiguration {
    public final static List<String> DEFAULT_CAPABILITY_TYPES = ImmutableList.of("capability", "feature", "story");
    protected static final String DEFAULT_ROOT_DIRECTORY = "stories";

    private final EnvironmentVariables environmentVariables;


    // Used for testing at this stage
    private String rootRequirementsDirectory = "/";

    public RequirementsConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<String> getRequirementTypes() {
        String requirementTypes = ThucydidesSystemProperty.THUCYDIDES_REQUIREMENT_TYPES.from(environmentVariables);
        List<String> types;
        if (StringUtils.isNotEmpty(requirementTypes)) {
            Iterator<String> typeValues = Splitter.on(",").trimResults().split(requirementTypes).iterator();
            types = Lists.newArrayList(typeValues);
        } else {
            types = getDefaultCapabilityTypes();
        }
        return types;
    }

    public String getDefaultRootDirectory() {
        if (ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.isDefinedIn(environmentVariables)) {
            return ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.from(environmentVariables);
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
            case 0: return ImmutableList.of("story");
            case 1: return ImmutableList.of("feature", "story");
            default: return ImmutableList.of("capability","feature", "story");
        }
    }

    private List<String> cucumberCapabilityTypes() {
        int featureDirectoryDepth = getCucumberFileMatcher().get().getMaxDepth();
        switch (featureDirectoryDepth) {
            case 0: return ImmutableList.of("feature");
            case 1: return ImmutableList.of("capability", "feature");
            default: return ImmutableList.of("theme","capability", "feature");
        }
    }

    SearchForFilesOfType jbehaveFileMatcher;

    private Optional<SearchForFilesOfType> getJBehaveFileMatcher() {
        if (jbehaveFileMatcher != null) {
            return Optional.of(jbehaveFileMatcher);
        }
        try {
            Path root = findResourcePath(rootRequirementsDirectory + "/stories");
            if (root != null) {
                jbehaveFileMatcher = new SearchForFilesOfType(root,".story");
                Files.walkFileTree(root, jbehaveFileMatcher);
                return Optional.of(jbehaveFileMatcher);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return Optional.absent();
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.absent();
        }
        return Optional.absent();
    }


    public String getRequirementType(int level) {
        return RequirementTypeAt.level(level).in(getRequirementTypes());
    }

    private Path findResourcePath(String path) throws URISyntaxException {
        Path root;
        URL storyDirectory = getClass().getResource(path);
        if (storyDirectory != null) {
            root =  Paths.get(storyDirectory.toURI());
        } else {
            root = Paths.get("src/test/resources/" + path);
        }
        return root;
    }
    SearchForFilesOfType cucumberFileMatcher;

    private Optional<SearchForFilesOfType> getCucumberFileMatcher() {
        if (cucumberFileMatcher != null) {
            return Optional.of(cucumberFileMatcher);
        }
        try {
            Path root = findResourcePath(rootRequirementsDirectory + "/features");
            if (root != null) {
                cucumberFileMatcher = new SearchForFilesOfType(root,".feature");
                Files.walkFileTree(root, cucumberFileMatcher);
                return Optional.of(cucumberFileMatcher);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return Optional.absent();
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.absent();
        }
        return Optional.absent();
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
