package net.thucydides.core.requirements;

import com.google.common.io.Resources;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.nio.file.Paths;

import static net.thucydides.core.requirements.FeatureOrStoryFile.FeatureOrStory.features;
import static net.thucydides.core.requirements.FeatureOrStoryFile.FeatureOrStory.stories;

public class FeatureOrStoryFile {

    enum FeatureOrStory {features, stories}

    private final String filename;
    private final FeatureOrStory type;
    private final EnvironmentVariables environmentVariables;

    public FeatureOrStoryFile(String filename, FeatureOrStory type) {
        this.filename = filename;
        this.type = type;
        this.environmentVariables = ConfiguredEnvironment.getEnvironmentVariables();
    }

    public static FeatureOrStoryFile forFeatureDescribedIn(String filename) {
        return new FeatureOrStoryFile(filename, features);
    }

    public static FeatureOrStoryFile forStoryDescribedIn(String filename) {
        return new FeatureOrStoryFile(filename, stories);
    }

    public File asFile() {
        if (Paths.get(filename).toFile().exists()) {
            return Paths.get(filename).toFile();
        }
        String requirementsDirectory = ThucydidesSystemProperty.THUCYDIDES_REQUIREMENTS_DIR.from(environmentVariables,type.name());
        String featureFileOnClassPath = (filename.startsWith(requirementsDirectory)) ? filename : requirementsDirectory + "/" + filename;
        try {
            return Paths.get(Resources.getResource(featureFileOnClassPath).getFile()).toFile();
        } catch (IllegalArgumentException featureFileNotOnClasspath) {
            return Paths.get(filename).toFile();
        }
    }


}
