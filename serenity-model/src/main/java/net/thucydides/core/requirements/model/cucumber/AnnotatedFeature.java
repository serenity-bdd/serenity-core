package net.thucydides.core.requirements.model.cucumber;



import io.cucumber.core.internal.gherkin.ast.Feature;
import io.cucumber.core.internal.gherkin.ast.ScenarioDefinition;

import java.util.List;

public class AnnotatedFeature {
    private final Feature feature;
    private final List<ScenarioDefinition> scenarioDefinitions;
    private final String descriptionInComments;

    public AnnotatedFeature(Feature feature, List<ScenarioDefinition> scenarioDefinitions, String descriptionInComments) {
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

    public List<ScenarioDefinition> getScenarioDefinitions() {
        return scenarioDefinitions;
    }
}
