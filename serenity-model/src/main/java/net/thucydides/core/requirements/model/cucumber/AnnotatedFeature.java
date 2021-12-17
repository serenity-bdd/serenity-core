package net.thucydides.core.requirements.model.cucumber;

import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.Scenario;

import java.util.List;

public class AnnotatedFeature {
    private final Feature feature;
    private final List<Scenario> scenarioDefinitions;
    private final String descriptionInComments;

    public AnnotatedFeature(Feature feature, List<Scenario> scenarioDefinitions, String descriptionInComments) {
        this.feature = feature;
        this.scenarioDefinitions = scenarioDefinitions;
        this.descriptionInComments = descriptionInComments;
    }

    public Feature getFeature() {
        return feature;
    }

    public String getDescriptionInComments() {
        return descriptionInComments;
    }

    public List<Scenario> getScenarioDefinitions() {
        return scenarioDefinitions;
    }
}
