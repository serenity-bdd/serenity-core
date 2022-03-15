package net.thucydides.core.requirements.classpath;

import com.google.common.io.Resources;
import net.thucydides.core.requirements.model.LoadedNarrative;
import net.thucydides.core.requirements.model.RequirementDefinition;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.replaceChars;

public class NarrativeTitle {

    private static final List<String> NARRATIVE_FILE_EXTENSIONS = Arrays.asList(".txt", ".md");

    public static String definedIn(String fullPath, String defaultType) {

        Optional<String> narrativePath = narrativePathsFor(fullPath);

//      String narrativePath = asResourcePath(fileSystemPathOfNarrativeInPackage(fullPath)) + ".txt";
        try {
            if (narrativePath.isPresent()) {
//             String narrativeFilePath = Resources.getResource(narrativePath).getFile();
//             if (new File(narrativeFilePath).exists()) {
                Optional<RequirementDefinition> narrative = LoadedNarrative.load().fromFile(new File(narrativePath.get()), defaultType);
                if (narrative.isPresent()) {
                    return (narrative.get().getTitle().orElse("") + System.lineSeparator() + narrative.get().getText()).trim();
                }
            }
        } catch (IllegalArgumentException noNarrativeFileFound) {
        }

        return "";
    }

    static Optional<String> narrativePathsFor(String fullPath) {
        return NARRATIVE_FILE_EXTENSIONS.stream()
                .map(extension -> asResourcePath(fileSystemPathOfNarrativeInPackage(fullPath)) + extension)
                .map(NarrativeTitle::filePathFor)
                .filter(filename -> !filename.isEmpty())
                .findFirst();
    }

    static String filePathFor(String narrativePath) {
        try {
            return Resources.getResource(narrativePath).getFile();
        } catch(IllegalArgumentException fileDoesNotExist) {
            return "";
        }
    }

    static String asResourcePath(String path) {
        return StringUtils.replace(path, File.separator, "/");
    }

    static String fileSystemPathOfNarrativeInPackage(String fullPath) {
        String narrativePath = (fullPath + File.separator + "narrative");
        return replaceChars(narrativePath, ".", File.separator);
    }

}
