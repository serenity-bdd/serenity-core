package net.thucydides.core.requirements.model;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.requirements.RequirementsPath;
import net.thucydides.core.requirements.model.cucumber.CucumberParser;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * Load a narrative text from a directory.
 * A narrative is a text file that describes a requirement, feature, or epic, or whatever terms you are using in your
 * project. The directory structure itself is used to organize capabilities into features, and so on. At the leaf
 * level, the directory will contain story files (e.g. JBehave stories, JUnit test cases, etc). At each level, a
 * "narrative.txt" file provides a description.
 */
public class NarrativeReader {
    private static final String BACKSLASH = "\\\\";
    private static final String FORWARDSLASH = "/";

    private final String rootDirectory;
    private final List<String> requirementTypes;

    protected NarrativeReader(String rootDirectory, List<String> requirementTypes) {
        this.rootDirectory = rootDirectory;
        this.requirementTypes = NewList.copyOf(requirementTypes);
    }

    public static NarrativeReader forRootDirectory(String rootDirectory) {
        return forRootDirectory(Injectors.getInjector().getInstance(EnvironmentVariables.class), rootDirectory);
    }


    public static NarrativeReader forRootDirectory(EnvironmentVariables environmentVariables, String rootDirectory) {
        List<String> requirementTypes = new RequirementsConfiguration(environmentVariables, rootDirectory).getRequirementTypes();
        return new NarrativeReader(rootDirectory, requirementTypes);
    }

    public NarrativeReader withRequirementTypes(List<String> requirementTypes) {
        return new NarrativeReader(this.rootDirectory, requirementTypes);
    }

    public Optional<RequirementDefinition> loadFrom(File directory) {
        return loadFrom(directory, 0);// levelOf(directory));
    }

    private int levelOf(File directory) {
        if (getClass().getClassLoader().getResource(rootDirectory) == null) {
            return 0;
        }
        try {
            String rootPath = getClass().getClassLoader().getResource(rootDirectory).toURI().getPath();
            if (directory.getPath().startsWith(rootPath)) {
                return directory.getPath()
                                .substring(rootPath.length() + 1)
                                .split(File.separator)
                                .length;
            }
        } catch (URISyntaxException e) {
            return 0;
        }
        return 0;

    }
    public Optional<RequirementDefinition> loadFrom(File directory, int requirementsLevel) {
        File[] narrativeFiles = directory.listFiles(calledNarrativeOrOverview());
        if (narrativeFiles == null || narrativeFiles.length == 0) {
            return Optional.empty();
        } else {
            return narrativeLoadedFrom(narrativeFiles[0], requirementsLevel);//max(0, requirementsLevel - 1));
        }
    }

    public Optional<RequirementDefinition> loadFromStoryFile(File storyFile) {
        if (storyFile.getName().endsWith(".story")) {
            return narrativeLoadedFrom(storyFile, "story");
        } else if (storyFile.getName().endsWith(".feature")) {
            return featureNarrativeLoadedFrom(storyFile);
        } else {
            return Optional.empty();
        }
    }

    private Optional<RequirementDefinition> narrativeLoadedFrom(File narrativeFile, int requirementsLevel) {
        String defaultType = directoryLevelInRequirementsHierarchy(narrativeFile, requirementsLevel);
        return LoadedNarrative.load().fromFile(narrativeFile, defaultType);
    }

    private Optional<RequirementDefinition> narrativeLoadedFrom(File narrativeFile, String defaultType) {
        return LoadedNarrative.load().fromFile(narrativeFile, defaultType);
    }


    private Optional<RequirementDefinition> featureNarrativeLoadedFrom(File narrativeFile)  {
        CucumberParser parser = new CucumberParser();
        return parser.loadFeatureDefinition(narrativeFile);
    }

    private String directoryLevelInRequirementsHierarchy(File narrativeFile, int requirementsLevel) {
        String normalizedNarrativePath = normalized(narrativeFile.getAbsolutePath());
        String normalizedRootPath = normalized(rootDirectory);
        int rootDirectoryStart = findRootDirectoryStart(normalizedNarrativePath, normalizedRootPath);
        int rootDirectoryEnd = findRootDirectoryEnd(rootDirectoryStart, normalizedNarrativePath, normalizedRootPath);
        String relativeNarrativePath = normalizedNarrativePath.substring(rootDirectoryEnd);
        int directoryCount = RequirementsPath.fileSystemPathElements(relativeNarrativePath).size() - 1;
        int level = calculateRequirementsLevel(requirementsLevel, directoryCount);

        return getRequirementTypeForLevel(level);
    }

    private boolean isNarrative(File narrativeFile) {
        return narrativeFile.getName().equalsIgnoreCase("readme.md")
                || narrativeFile.getName().equalsIgnoreCase("readme.txt")
                || narrativeFile.getName().toLowerCase().endsWith("narrative.txt")
                || narrativeFile.getName().toLowerCase().endsWith("narrative.md");
    }

    private int calculateRequirementsLevel(int requirementsLevel, int directoryCount) {
        return requirementsLevel + directoryCount - 1;
    }

    private int findRootDirectoryEnd(int rootDirectoryStart, String normalizedNarrativePath, String normalizedRootPath) {
        if (normalizedNarrativePath.contains(normalizedRootPath)) {
            return (rootDirectoryStart >= 0) ? rootDirectoryStart + normalizedRootPath.length() : 0;
        } else if (normalizedNarrativePath.contains("/stories/")) {
            return (rootDirectoryStart >= 0) ? rootDirectoryStart + "/stories/".length() : 0;
        } else if (normalizedNarrativePath.contains("/features/")) {
            return (rootDirectoryStart >= 0) ? rootDirectoryStart + "/features/".length() : 0;
        }
        return 0;
    }

    private int findRootDirectoryStart(String normalizedNarrativePath, String normalizedRootPath) {
        if (normalizedNarrativePath.contains(normalizedRootPath)) {
            return normalizedNarrativePath.indexOf(normalizedRootPath);
        } else if (normalizedNarrativePath.contains("/stories/")) {
            return normalizedNarrativePath.indexOf("/stories/");
        } else if (normalizedNarrativePath.contains("/features/")) {
            return normalizedNarrativePath.indexOf("/features/");
        }
        return 0;
    }

    private String normalized(String path) {
        return path.replaceAll(BACKSLASH, FORWARDSLASH);
    }

    private String getRequirementTypeForLevel(int level) {
        if (level > requirementTypes.size() - 1) {
            return requirementTypes.get(requirementTypes.size() - 1);
        } else {
            return requirementTypes.get(level);
        }
    }

    private FilenameFilter calledNarrativeOrOverview() {
        return (file, name) -> (name.toLowerCase().equals("narrative.txt")
                                || name.toLowerCase().equals("narrative.md")
                                || name.toLowerCase().equals("readme.md")
                                || name.toLowerCase().equals("overview.txt")
                                || name.toLowerCase().equals("overview.md"));
    }
}
