package net.thucydides.model.requirements;

import com.google.common.base.Splitter;
import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.util.EnvironmentVariables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_REQUIREMENT_TYPES;

public class DefaultCapabilityTypes {

    public final static List<String> DEFAULT_CAPABILITY_TYPES = NewList.of("capability", "feature", "story");
    private final static DefaultCapabilityTypes INSTANCE = new DefaultCapabilityTypes();
    private List<String> defaultCapabilityTypes;

    private SearchForFilesOfType cucumberFileMatcher;
    private SearchForFilesOfType jbehaveFileMatcher;
    private SearchForFilesOfType javaScriptSpecMatcher;

    private Map<String, List<String>> requirementsCache = new HashMap<>();
    public static DefaultCapabilityTypes instance() {
        return INSTANCE;
    }

    private final static String JAVASCRIPT_SPEC_FILE_EXTENSION_PATTERN =
            "\\.(spec|test|integration|it|e2e|spec\\.e2e|spec-e2e)" +   // Consider only test files...
            "\\.(jsx?|mjsx?|cjsx?|tsx?|mtsx?|ctsx?)$";                  // implemented in either JavaScript or TypeScript

    private final static String JAVASCRIPT_SPEC_FILE_NAME_PATTERN =
            "^(?!.*/(node_modules|jspm_packages|web_modules)/)" +       // Ignore external dependencies
            ".*" +
            JAVASCRIPT_SPEC_FILE_EXTENSION_PATTERN;

    public void clear() {
        requirementsCache.clear();
        defaultCapabilityTypes = null;
        jbehaveFileMatcher = null;
        cucumberFileMatcher = null;
        javaScriptSpecMatcher = null;
    }

    public List<String> getRequirementTypes(EnvironmentVariables environmentVariables, Optional<Path> root) {
        if (requirementsCache.containsKey(key(environmentVariables,root))) {
            return requirementsCache.get(key(environmentVariables,root));
        } else {
            synchronized (requirementsCache) {
                List<String> requriementTypes = configuredRequirementTypes(environmentVariables).orElse(getDefaultCapabilityTypes(root));
                requirementsCache.put(key(environmentVariables, root), requriementTypes);
                return requriementTypes;
            }
        }
    }
    public List<String> getDefaultCapabilityTypes(Optional<Path> root) {
        if (defaultCapabilityTypes == null) {
            if (jbehaveFilesExist(root)) {
                defaultCapabilityTypes = jbehaveCapabilityTypes(root);
            }
            else if (cucumberFilesExist(root)) {
                defaultCapabilityTypes = cucumberCapabilityTypes(root);
            }
            else if(javaScriptSpecsExist(root)) {
                defaultCapabilityTypes = javaScriptCapabilityHierararchy(root);
            }
            else {
                defaultCapabilityTypes = DEFAULT_CAPABILITY_TYPES;
            }
        }
        return defaultCapabilityTypes;
    }

    private List<String> jbehaveCapabilityTypes(Optional<Path> root) {
        int featureDirectoryDepth = getJBehaveFileMatcher(root).get().getMaxDepth();
        switch (featureDirectoryDepth) {
            case 0:
                return NewList.of("story");
            case 1:
                return NewList.of("feature", "story");
            default:
                return NewList.of("capability", "feature", "story");
        }
    }

    private List<String> cucumberCapabilityTypes(Optional<Path> root) {
        int featureDirectoryDepth = getCucumberFileMatcher(root).get().getMaxDepth();
        switch (featureDirectoryDepth) {
            case 0:
                return NewList.of("feature");
            case 1:
                return NewList.of("capability", "feature");
            default:
                return NewList.of("theme", "capability", "feature");
        }
    }

    private List<String> javaScriptCapabilityHierararchy(Optional<Path> root) {
        int directoryHierarchyDepth = getJavaScriptSpecMatcher(root).get().getMaxDepth();
        switch (directoryHierarchyDepth) {
            case 0:
                return NewList.of("feature");
            case 1:
                return NewList.of("capability", "feature");
            default:
                return NewList.of("theme", "capability", "feature");
        }
    }

    private Optional<SearchForFilesOfType> getJBehaveFileMatcher(Optional<Path> root) {
        if (jbehaveFileMatcher != null) {
            return Optional.of(jbehaveFileMatcher);
        }
        try {
//            Optional<Path> root = RootDirectory.definedIn(environmentVariables).featuresOrStoriesRootDirectory();// findResourcePath(rootRequirementsDirectory + "/stories");
            if (root.isPresent()) {
                jbehaveFileMatcher = new SearchForFilesOfType(root.get(), ".story");
                Files.walkFileTree(root.get(), jbehaveFileMatcher);
                return Optional.of(jbehaveFileMatcher);
            }
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    private Optional<SearchForFilesOfType> getJavaScriptSpecMatcher(Optional<Path> root) {
        if (javaScriptSpecMatcher != null) {
            return Optional.of(javaScriptSpecMatcher);
        }

        try {
            if (! root.isPresent()) {
                return Optional.empty();
            }

            javaScriptSpecMatcher = new SearchForFilesOfType(root.get(), Pattern.compile(JAVASCRIPT_SPEC_FILE_NAME_PATTERN));
            Files.walkFileTree(root.get(), javaScriptSpecMatcher);
            return Optional.of(javaScriptSpecMatcher);
        }
        catch (IOException e) {
            return Optional.empty();
        }
    }

    private boolean jbehaveFilesExist(Optional<Path> root) {
        return (getJBehaveFileMatcher(root).isPresent() && getJBehaveFileMatcher(root).get().hasMatchingFiles());
    }

    private boolean cucumberFilesExist(Optional<Path> root) {
        return (getCucumberFileMatcher(root).isPresent() && getCucumberFileMatcher(root).get().hasMatchingFiles());
    }

    private boolean javaScriptSpecsExist(Optional<Path> root) {
        return (getJavaScriptSpecMatcher(root).isPresent() && getJavaScriptSpecMatcher(root).get().hasMatchingFiles());
    }

    public int startLevelForADepthOf(Optional<Path> root, EnvironmentVariables environmentVariables, int requirementsDepth) {
        return Math.max(0, getRequirementTypes(environmentVariables, root).size() - requirementsDepth);
    }

    private Optional<SearchForFilesOfType> getCucumberFileMatcher(Optional<Path> root) {
        if (cucumberFileMatcher != null) {
            return Optional.of(cucumberFileMatcher);
        }
        try {
//            Optional<Path> root = RootDirectory.definedIn(environmentVariables).featuresOrStoriesRootDirectory();// findResourcePath(rootRequirementsDirectory + "/stories");
            if (root.isPresent()) {
                cucumberFileMatcher = new SearchForFilesOfType(root.get(), ".feature");
                Files.walkFileTree(root.get(), cucumberFileMatcher);
                return Optional.of(cucumberFileMatcher);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.empty();
    }

    private Optional<List<String>> configuredRequirementTypes(EnvironmentVariables environmentVariables) {

        if (SERENITY_REQUIREMENT_TYPES.isDefinedIn(environmentVariables)) {
            String configuredRequirementTypes = SERENITY_REQUIREMENT_TYPES.from(environmentVariables);
            return Optional.of(Splitter.on(",").trimResults().splitToList(configuredRequirementTypes));
        }
        return Optional.empty();
    }

    private String key(EnvironmentVariables environmentVariables, Optional<Path> root) {
        return SERENITY_REQUIREMENT_TYPES.optionalFrom(environmentVariables).orElse("DEFAULT")
                + root.orElse(Paths.get("default"));
    }
}
