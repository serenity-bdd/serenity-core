package net.serenitybdd.cucumber.util;

import io.cucumber.core.resource.ClasspathSupport;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Objects;

public class PathUtils {

    private PathUtils() {
    }

    public static File getAsFile(URI cucumberFeatureUri) {
        Objects.requireNonNull(cucumberFeatureUri, "cucumber feature URI cannot be null");
        String featureFilePath;
        switch (cucumberFeatureUri.getScheme()) {
            case "file": {
                try {
                    featureFilePath = cucumberFeatureUri.toURL().getPath();
                    break;
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("Cannot convert cucumber feature URI to URL", e);
                }
            }
            case "classpath": {
                featureFilePath = ClasspathSupport.resourceName(cucumberFeatureUri);
                break;
            }
            default:
                throw new IllegalArgumentException("Cannot get cucumber feature file from URI");
        }
        return new File(featureFilePath);
    }

    public static File getAsFile(String cucumberFeatureUri) {
        return getAsFile(URI.create(cucumberFeatureUri));
    }
}
