package net.thucydides.core.requirements.classpath;

import com.google.common.io.Resources;
import net.thucydides.core.requirements.model.LoadedNarrative;
import net.thucydides.core.requirements.model.Narrative;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.replaceChars;

/**
 * Created by john on 13/07/2016.
 */
public class NarrativeText {

    public static String definedIn(String fullPath, String type) {
        String narrativePath = asResourcePath(fileSystemPathOfNarrativeInPackage(fullPath)) + ".txt";
        try {
            String narrativeFilePath = Resources.getResource(narrativePath).getFile();
            if (new File(narrativeFilePath).exists()) {
                Optional<Narrative> narrative = LoadedNarrative.load().fromFile(new File(narrativeFilePath), type);
                if (narrative.isPresent()) {
                    return (narrative.get().getTitle().or("") + System.lineSeparator() + narrative.get().getText()).trim();
                }
            }
        } catch(IllegalArgumentException noNarrativeFileFound){}

        return "";
    }

    private static String asResourcePath(String path) {
        return StringUtils.replace(path,File.separator, "/");
    }

    private static String fileSystemPathOfNarrativeInPackage(String fullPath) {
        String narrativePath = (fullPath + File.separator + "narrative");
        return replaceChars(narrativePath,".", File.separator);
    }

}