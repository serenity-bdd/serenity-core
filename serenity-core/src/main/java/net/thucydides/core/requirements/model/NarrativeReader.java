package net.thucydides.core.requirements.model;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.requirements.model.cucumber.CucumberParser;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.List;

import static net.thucydides.core.requirements.RequirementsPath.fileSystemPathElements;

/**
 * Load a narrative text from a directory.
 * A narrative is a text file that describes a requirement, feature, or epic, or whatever terms you are using in your
 * project. The directory structure itself is used to organize capabilities into features, and so on. At the leaf
 * level, the directory will contain story files (e.g. JBehave stories, JUnit test cases, etc). At each level, a
 * "narrative.txt" file provides a description.
 */
public class NarrativeReader {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String BACKSLASH = "\\\\";
    private static final String FORWARDSLASH = "/";

    private final String rootDirectory;
    private final List<String> requirementTypes;

    protected NarrativeReader(String rootDirectory, List<String> requirementTypes) {
        this.rootDirectory = rootDirectory;
        this.requirementTypes = ImmutableList.copyOf(requirementTypes);
    }

    public static NarrativeReader forRootDirectory(String rootDirectory) {
        return new NarrativeReader(rootDirectory, RequirementsConfiguration.DEFAULT_CAPABILITY_TYPES);
    }

    public NarrativeReader withRequirementTypes(List<String> requirementTypes) {
        return new NarrativeReader(this.rootDirectory, requirementTypes);
    }

    public Optional<Narrative> loadFrom(File directory) {
        return loadFrom(directory, 0);
    }

    public Optional<Narrative> loadFrom(File directory, int requirementsLevel) {
        File[] narrativeFiles = directory.listFiles(calledNarrativeDotTxt());
        if (narrativeFiles.length == 0) {
            return Optional.absent();
        } else {
            return narrativeLoadedFrom(narrativeFiles[0], requirementsLevel);
        }
    }


    public Optional<Narrative> loadFromStoryFile(File storyFile) {
        if (storyFile.getName().endsWith(".story")) {
            return narrativeLoadedFrom(storyFile, "story");
        } else if (storyFile.getName().endsWith(".feature")) {
            return featureNarrativeLoadedFrom(storyFile);
        } else {
            return Optional.absent();
        }
    }

    private Optional<Narrative> narrativeLoadedFrom(File narrativeFile, int requirementsLevel) {
        String defaultType = directoryLevelInRequirementsHierarchy(narrativeFile, requirementsLevel);
        return narrativeLoadedFrom(narrativeFile, defaultType);
    }

    private Optional<Narrative> narrativeLoadedFrom(File narrativeFile, String defaultType) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(narrativeFile), "UTF-8"))) {
            List<String> lines = readPreambleFrom(reader);

            String title = null;
            String type = defaultType;
            String cardNumber = findCardNumberIn(lines);
            List<String> versionNumbers = findVersionNumberIn(lines);
            Optional<String> titleLine = readOptionalTitleFrom(lines);
            if (titleLine.isPresent()) {
                title = titleFrom(titleLine.get());
                type = typeFrom(titleLine.get()).or(defaultType);
            }
            String text = readNarrativeFrom(lines);
            reader.close();
            return Optional.of(new Narrative(Optional.fromNullable(title),
                    Optional.fromNullable(cardNumber),
                    versionNumbers,
                    type, text));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return Optional.absent();
    }

    private String titleFrom(String titleLine) {
        return (titleLine.contains(":")) ? titleLine.substring(titleLine.indexOf(":") + 1) : titleLine;
    }

    Optional<String> TYPE_UNDEFINED = Optional.absent();

    private Optional<String> typeFrom(String titleLine) {
        if (titleLine.contains(":")) {
            String featureNameSection = titleLine.substring(0, titleLine.indexOf(":"));
            String featureName = featureNameSection.split("\\s")[0];
            return Optional.of(featureName.toLowerCase().trim());
        } else {
            return TYPE_UNDEFINED;
        }
    }

    private Optional<Narrative> featureNarrativeLoadedFrom(File narrativeFile)  {
        CucumberParser parser = new CucumberParser();
        return parser.loadFeatureNarrative(narrativeFile);
    }


    private String findCardNumberIn(List<String> lines) {
        String cardNumber = null;
        for (String line : lines) {
            String normalizedLine = line.toUpperCase();
            if (normalizedLine.startsWith("@ISSUES")) {
                String issueList = normalizedLine.replace("@ISSUES", "").trim();
                List<String> issues = Lists.newArrayList(Splitter.on(",").trimResults().split(issueList));
                if (!issues.isEmpty()) {
                    cardNumber = issues.get(0);
                }
            } else if (normalizedLine.startsWith("@ISSUE")) {
                String issueNumber = normalizedLine.replace("@ISSUE", "").trim();
                if (!StringUtils.isEmpty(issueNumber)) {
                    cardNumber = issueNumber;
                }
            }
        }
        return cardNumber;
    }

    private List<String> findVersionNumberIn(List<String> lines) {
        for (String line : lines) {
            String normalizedLine = line.toUpperCase();
            if (normalizedLine.startsWith("@VERSIONS")) {
                String versionList = line.substring("@VERSIONS".length()).trim();
                return Lists.newArrayList(Splitter.on(",").trimResults().split(versionList));
            }
        }
        return ImmutableList.of();
    }

    private String readNarrativeFrom(List<String> lines) {

        StringBuilder description = new StringBuilder();
        for (String line : lines) {
            if (!isMarkup(line) && !isAnnotation(line) && !(isComment(line))) {
                description.append(line.trim());
                description.append(NEW_LINE);
            }
        }
        return description.toString().trim();

    }

    private boolean isAnnotation(String line) {
        return normalizedLine(line).startsWith("@");
    }

    private Optional<String> readOptionalTitleFrom(List<String> lines) {
        if (!lines.isEmpty()) {
            String firstLine = lines.get(0);
            if (!isMarkup(firstLine)) {
                lines.remove(0);
                return Optional.of(stripFeatureFrom(firstLine));
            }
        }
        return Optional.absent();
    }

    private String stripFeatureFrom(String firstLine) {
        return (firstLine.toLowerCase().startsWith("feature:")) ? firstLine.substring(8).trim() : firstLine;
    }

    private boolean isComment(String line) {
        return line.startsWith("#");
    }

    private boolean isMarkup(String line) {
        String normalizedLine = normalizedLine(line);
        return normalizedLine.startsWith("narrative:")
                || normalizedLine.startsWith("givenstory:")
                || normalizedLine.startsWith("background:")
                || normalizedLine.startsWith("meta")
                || normalizedLine.startsWith("@")
                || normalizedLine.startsWith("givenstories:");
    }

    private List<String> readPreambleFrom(BufferedReader reader) throws IOException {
        List<String> usefulLines = Lists.newArrayList();

        boolean preambleFinished = false;
        while (!preambleFinished) {
            String nextLine = reader.readLine();
            if (nextLine == null) {
                preambleFinished = true;
            } else {
                if (preambleFinishedAt(nextLine)) {
                    preambleFinished = true;
                } else if (thereIsUsefulInformationIn(nextLine)) {
                    usefulLines.add(nextLine);
                }
            }
        }
        return usefulLines;
    }

    private boolean preambleFinishedAt(String nextLine) {
        return (normalizedLine(nextLine).startsWith("scenario")
                        || normalizedLine(nextLine).startsWith("background:"));
    }

    private String normalizedLine(String nextLine) {
        return nextLine.trim().toLowerCase();
    }

    private boolean thereIsUsefulInformationIn(String nextLine) {
        String normalizedText = normalizedLine(nextLine);
        return !normalizedText.isEmpty()
                && !normalizedText.startsWith("#")
                && !normalizedText.startsWith("meta:")
                && !normalizedText.startsWith("background:")
                && !(normalizedText.startsWith("@")
                && (!normalizedText.startsWith("@issue")
                && (!normalizedText.startsWith("@versions"))));
    }

    private String directoryLevelInRequirementsHierarchy(File narrativeFile, int requirementsLevel) {
        String normalizedNarrativePath = normalized(narrativeFile.getAbsolutePath());
        String normalizedRootPath = normalized(rootDirectory);
        int rootDirectoryStart = findRootDirectoryStart(normalizedNarrativePath, normalizedRootPath);
        int rootDirectoryEnd = findRootDirectoryEnd(rootDirectoryStart, normalizedNarrativePath, normalizedRootPath);
        String relativeNarrativePath = normalizedNarrativePath.substring(rootDirectoryEnd);
        int directoryCount = fileSystemPathElements(relativeNarrativePath).size() - 1;
        int level = requirementsLevel + directoryCount - 1;

        return getRequirementTypeForLevel(level);
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
            return normalizedNarrativePath.lastIndexOf(normalizedRootPath);
        } else if (normalizedNarrativePath.contains("/stories/")) {
            return normalizedNarrativePath.lastIndexOf("/stories/");
        } else if (normalizedNarrativePath.contains("/features/")) {
            return normalizedNarrativePath.lastIndexOf("/features/");
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

    private FilenameFilter calledNarrativeDotTxt() {
        return new FilenameFilter() {

            public boolean accept(File file, String name) {
                return name.toLowerCase().equals("narrative.txt");
            }
        };
    }
}
