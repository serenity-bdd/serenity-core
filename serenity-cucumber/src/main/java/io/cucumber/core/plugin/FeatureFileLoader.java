package io.cucumber.core.plugin;


import io.cucumber.messages.types.Feature;
import io.cucumber.plugin.event.TestSourceRead;
import net.thucydides.core.util.Inflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class FeatureFileLoader {

    private final TestSourcesModel testSources = new TestSourcesModel();

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureFileLoader.class);

    private Optional<Feature> featureFrom(URI featureFileUri) {

        String defaultFeatureId = new File(featureFileUri).getName().replace(".feature", "");
        String defaultFeatureName = Inflector.getInstance().humanize(defaultFeatureId);

        parseGherkinIn(featureFileUri);

        if (isEmpty(testSources.getFeature(featureFileUri).getName())) {
            return Optional.empty();
        }

        Feature feature = testSources.getFeature(featureFileUri);
        if (feature.getName().isEmpty()) {
            feature = featureWithDefaultName(feature, defaultFeatureName);
        }
        return Optional.of(feature);
    }

    private void parseGherkinIn(URI featureFileUri) {
        try {
            testSources.getFeature(featureFileUri);
        } catch (Throwable ignoreParsingErrors) {
            LOGGER.warn("Could not parse the Gherkin in feature file " + featureFileUri + ": file ignored");
        }
    }

    public Feature featureWithDefaultName(Feature feature, String defaultName) {
        return new Feature(feature.getLocation(),
                feature.getTags(),
                feature.getLanguage(),
                feature.getKeyword(),
                defaultName,
                feature.getDescription(),
                feature.getChildren());
    }

    public void addTestSourceReadEvent(TestSourceRead event) {
        testSources.addTestSourceReadEvent(event.getUri(), event);
    }

    public String getFeatureName(URI featureFileUri) {
        return testSources.getFeature(featureFileUri).getName();
    }

    public Feature getFeature(URI featureFileUri) {
        return testSources.getFeature(featureFileUri);
    }

    TestSourcesModel.AstNode getAstNode(URI path, int line) {
        return testSources.getAstNode(path, line);
    }
}
