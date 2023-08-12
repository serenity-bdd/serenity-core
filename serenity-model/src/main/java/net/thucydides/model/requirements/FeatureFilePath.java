package net.thucydides.model.requirements;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureFilePath {

    private final String featureFolder;

    public FeatureFilePath(EnvironmentVariables environmentVariables) {
        this.featureFolder = EnvironmentSpecificConfiguration
                                .from(environmentVariables).getOptionalProperty(ThucydidesSystemProperty.SERENITY_FEATURES_DIRECTORY)
                                .orElse("features");
    }

    /**
     * Given a path to a feature file, return the path relative to the last mention of the feature folder.
     * @param path The full path to the feature file
     * @return The path relative to the last mention of the feature folder
     */
    public String relativePathFor(String path) {
        int lastFeatures = path.lastIndexOf("/" + featureFolder + "/");
        if (lastFeatures >= 0) {
            return path.substring(lastFeatures + featureFolder.length() + 2);
        } else {
            return relativeFeaturePath(path);
        }
    }

    private static String FEATURE_FILES_DIRECTORY = "src/test/resources/[^/]+/";
    private final static Pattern FEATURE_FILES_DIRECTORY_PATTERN = Pattern.compile(FEATURE_FILES_DIRECTORY);

    private String relativeFeaturePath(String path) {
        String normalisedPath = path;
        Matcher matcher = FEATURE_FILES_DIRECTORY_PATTERN.matcher(path);
        if (matcher.find()) {
            normalisedPath = path.substring(matcher.end());
        }
        return normalisedPath;
    }

}
