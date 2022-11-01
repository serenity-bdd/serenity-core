package net.thucydides.core.requirements.reports.cucumber;

import net.thucydides.core.requirements.model.cucumber.AnnotatedFeature;
import net.thucydides.core.requirements.model.cucumber.CucumberParser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FeatureCache {
    private static final FeatureCache FEATURE_CACHE = new FeatureCache();

    private final CucumberParser parser = new CucumberParser();
    private Map<File, Optional<AnnotatedFeature>> loadedFiles = new HashMap<>();

    public static FeatureCache getCache() {
        return FEATURE_CACHE;
    }

    public Optional<AnnotatedFeature> loadFeature(File featureFile) {
        if (loadedFiles.containsKey(featureFile)) {
            return loadedFiles.get(featureFile);
        } else {
            Optional<AnnotatedFeature> feature = parser.loadFeature(featureFile);
            loadedFiles.put(featureFile, feature);
            return feature;
        }
    }
}
